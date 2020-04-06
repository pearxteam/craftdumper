package net.pearx.craftdumper.server

import kotlinx.coroutines.*
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.network.PacketDistributor
import net.minecraftforge.fml.server.ServerLifecycleHooks
import net.pearx.craftdumper.ID
import net.pearx.craftdumper.common.CommonProxy
import net.pearx.craftdumper.common.command.CraftDumperCommand
import net.pearx.craftdumper.common.command.DumpType
import net.pearx.craftdumper.common.dumper.DumpProgressReporter
import net.pearx.craftdumper.common.dumper.Dumper
import net.pearx.craftdumper.common.helper.internal.getTotalCount
import net.pearx.craftdumper.common.network.NETWORK
import net.pearx.craftdumper.common.network.message.CPacketCreateToast
import net.pearx.craftdumper.common.network.message.CPacketHideToast
import net.pearx.craftdumper.common.network.message.CPacketUpdateToast
import java.io.File
import java.util.*

@OnlyIn(Dist.DEDICATED_SERVER)
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

    override fun createDump(source: CommandSource, toDump: List<Pair<Dumper, DumpType>>) {
        if (toDump.isEmpty())
            return

        val sender = source.entity

        val token = nextToastToken()
        if (sender is ServerPlayerEntity)
            NETWORK.send(PacketDistributor.PLAYER.with { sender }, CPacketCreateToast(token))

        GlobalScope.launch {
            val totalCount = toDump.getTotalCount()
            var prevCount = 0
            val reporter = object : DumpProgressReporter {
                override var progress: Int = 0
            }
            for ((dumper, type) in toDump) {
                if (type.data) {
                    var updateCoroutine: Deferred<Unit>? = null
                    if (sender is ServerPlayerEntity) {
                        source.server.deferTask {
                            NETWORK.send(PacketDistributor.PLAYER.with { sender }, CPacketUpdateToast(token, prevCount.toFloat() / totalCount, dumper.getTitle(), dumper.getSubtitleData()))
                        }
                        updateCoroutine =
                            async {
                                while (true) {
                                    source.server.deferTask {
                                        NETWORK.send(PacketDistributor.PLAYER.with { sender }, CPacketUpdateToast(token, (prevCount + reporter.progress.toFloat()) / totalCount, dumper.getTitle(), dumper.getSubtitleData()))
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
                    if (sender is ServerPlayerEntity) {
                        source.server.deferTask {
                            NETWORK.send(PacketDistributor.PLAYER.with { sender }, CPacketUpdateToast(token, (prevCount + 1F) / totalCount, subtitle = dumper.getSubtitleAmounts()))
                        }
                    }
                    dumper.dumpAmounts()
                    prevCount++
                }
                source.server.deferTask {
                    for (msg in CraftDumperCommand.createSuccessMessage(dumper, listOf(), true))
                        source.sendFeedback(msg, true)
                }
            }
            if (sender is ServerPlayerEntity) {
                source.server.deferTask {
                    NETWORK.send(PacketDistributor.PLAYER.with { sender }, CPacketHideToast(token))
                    freeToken(token)
                }
            }
        }
    }

    override val outputDirectory: File
        get() = ServerLifecycleHooks.getCurrentServer().dataDirectory.resolve(ID)
}