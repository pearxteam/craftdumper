package net.pearx.craftdumper.server

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    override fun createDump(command: CraftDumperCommand, sender: ICommandSender, dumper: Dumper, type: CraftDumperCommand.DumpType) {
        with(command) {
            val token = nextToastToken()
            val reporter = object : DumpProgressReporter {
                override var progress: Float = 0F
            }
            if (sender is EntityPlayerMP)
                CHANNEL.sendTo(CPacketCreateToast(token, dumper, "data"), sender)

            GlobalScope.launch {
                if (type.data) {
                    val updateCoroutine = if (sender is EntityPlayerMP)
                        async {
                            while (true) {
                                (sender.entityWorld as WorldServer).addScheduledTask {
                                    CHANNEL.sendTo(CPacketUpdateToast(token, reporter.progress), sender)
                                }
                                delay(500)
                            }
                        }
                    else null
                    dumper.dumpData(reporter)
                    updateCoroutine?.cancel()
                }
                if (type.amounts) {
                    if (sender is EntityPlayerMP) {
                        (sender.entityWorld as WorldServer).addScheduledTask {
                            CHANNEL.sendTo(CPacketUpdateToast(token, 1F, "amounts"), sender)
                        }
                    }
                    dumper.dumpAmounts()
                }
                (sender.entityWorld as WorldServer).addScheduledTask {
                    if (sender is EntityPlayerMP) {
                        CHANNEL.sendTo(CPacketHideToast(token), sender)
                        freeToken(token)
                    }
                    for (msg in createSuccessMessage(dumper, listOf(), true))
                        sender.sendMessage(msg)
                }
            }
        }
    }
}