package net.pearx.craftdumper.server

import kotlinx.coroutines.*
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.WorldServer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftdumper.common.CommonProxy
import net.pearx.craftdumper.common.CraftDumperCommand
import net.pearx.craftdumper.common.dumper.DumpProgressReporter
import net.pearx.craftdumper.common.dumper.Dumper
import net.pearx.craftdumper.common.helper.internal.getTotalCount
import net.pearx.craftdumper.common.network.NETWORK
import net.pearx.craftdumper.common.network.message.CPacketCreateToast
import net.pearx.craftdumper.common.network.message.CPacketHideToast
import net.pearx.craftdumper.common.network.message.CPacketUpdateToast
import java.util.*

@SideOnly(Side.SERVER)
class ServerProxy : CommonProxy {
    private val takenToastTokens = TreeSet<Int>()

    private fun nextToastToken(): Int {
        var prev = -1
        for (el in takenToastTokens) {
            if (el - prev > 1) {
                takenToastTokens += prev + 1
                return prev + 1
            }
            prev = el
        }
        takenToastTokens += prev + 1
        return prev + 1
    }

    private fun freeToken(i: Int) {
        takenToastTokens -= i
    }

    override fun createDump(command: CraftDumperCommand, sender: ICommandSender, toDump: List<Pair<Dumper, CraftDumperCommand.DumpType>>) {
        if (toDump.isEmpty())
            return
        with(command) {
            val token = nextToastToken()
            if (sender is EntityPlayerMP)
                NETWORK.sendTo(CPacketCreateToast(token), sender)

            GlobalScope.launch {
                val totalCount = toDump.getTotalCount()
                var prevCount = 0
                val reporter = object : DumpProgressReporter {
                    override var progress: Int = 0
                }
                for((dumper, type) in toDump) {
                    if (type.data) {
                        var updateCoroutine: Deferred<Unit>? = null
                        if (sender is EntityPlayerMP) {
                            (sender.entityWorld as WorldServer).addScheduledTask {
                                NETWORK.sendTo(CPacketUpdateToast(token, prevCount.toFloat() / totalCount, dumper.getTitle(), dumper.getSubtitleData()), sender)
                            }
                            updateCoroutine =
                                async {
                                    while (true) {
                                        (sender.entityWorld as WorldServer).addScheduledTask {
                                            NETWORK.sendTo(CPacketUpdateToast(token, (prevCount + reporter.progress.toFloat()) / totalCount, dumper.getTitle(), dumper.getSubtitleData()), sender)
                                        }
                                        delay(500)
                                    }
                                }
                        }
                        dumper.dumpData(reporter)
                        prevCount += reporter.progress
                        updateCoroutine?.cancel()
                    }
                    if (type.amounts) {
                        if (sender is EntityPlayerMP) {
                            (sender.entityWorld as WorldServer).addScheduledTask {
                                NETWORK.sendTo(CPacketUpdateToast(token, (prevCount + 1F) / totalCount, subtitle = dumper.getSubtitleAmounts()), sender)
                            }
                        }
                        dumper.dumpAmounts()
                        prevCount++
                    }
                    (sender.entityWorld as WorldServer).addScheduledTask {
                        for (msg in createSuccessMessage(dumper, listOf(), true))
                            sender.sendMessage(msg)
                    }
                }
                if (sender is EntityPlayerMP) {
                    (sender.entityWorld as WorldServer).addScheduledTask {
                        NETWORK.sendTo(CPacketHideToast(token), sender)
                        freeToken(token)
                    }
                }
            }
        }
    }
}