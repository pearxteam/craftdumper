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
import net.pearx.craftdumper.common.network.CHANNEL
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
            val totalProgress = toDump.size.toFloat()
            val reporter = object : DumpProgressReporter {
                override var progress: Float = 0F
            }
            if (sender is EntityPlayerMP)
                CHANNEL.sendTo(CPacketCreateToast(token), sender)

            GlobalScope.launch {
                toDump.forEachIndexed { index, (dumper, type) ->
                    if (type.data) {
                        var updateCoroutine: Deferred<Unit>? = null
                        if (sender is EntityPlayerMP) {
                            (sender.entityWorld as WorldServer).addScheduledTask {
                                CHANNEL.sendTo(CPacketUpdateToast(token, index.toFloat() / totalProgress, dumper.getTitle(), dumper.getSubtitleData()), sender)
                            }
                            updateCoroutine =
                                async {
                                    while (true) {
                                        (sender.entityWorld as WorldServer).addScheduledTask {
                                            CHANNEL.sendTo(CPacketUpdateToast(token, (index + reporter.progress) / totalProgress, dumper.getTitle(), dumper.getSubtitleData()), sender)
                                        }
                                        delay(500)
                                    }
                                }
                        }
                        dumper.dumpData(reporter)
                        updateCoroutine?.cancel()
                    }
                    if (type.amounts) {
                        if (sender is EntityPlayerMP) {
                            (sender.entityWorld as WorldServer).addScheduledTask {
                                CHANNEL.sendTo(CPacketUpdateToast(token, (index + 1F) / totalProgress, subtitle = dumper.getSubtitleAmounts()), sender)
                            }
                        }
                        dumper.dumpAmounts()
                    }
                    (sender.entityWorld as WorldServer).addScheduledTask {
                        for (msg in createSuccessMessage(dumper, listOf(), true))
                            sender.sendMessage(msg)
                    }
                }
                if (sender is EntityPlayerMP) {
                    (sender.entityWorld as WorldServer).addScheduledTask {
                        CHANNEL.sendTo(CPacketHideToast(token), sender)
                        freeToken(token)
                    }
                }
            }
        }
    }
}