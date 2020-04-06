package net.pearx.craftdumper.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandSource
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.pearx.craftdumper.ID
import net.pearx.craftdumper.common.CommonProxy
import net.pearx.craftdumper.common.command.CraftDumperCommand
import net.pearx.craftdumper.common.command.DumpType
import net.pearx.craftdumper.common.dumper.DumpOutput
import net.pearx.craftdumper.common.dumper.DumpProgressReporter
import net.pearx.craftdumper.common.dumper.Dumper
import net.pearx.craftdumper.common.helper.internal.getTotalCount
import java.io.File

@OnlyIn(Dist.CLIENT)
class ClientProxy : CommonProxy {
    override fun createDump(source: CommandSource, toDump: List<Pair<Dumper, DumpType>>) {
        Minecraft.getInstance().deferTask {
            val toast = DumperToast(-1)
            Minecraft.getInstance().toastGui.add(toast)
            GlobalScope.launch {
                val totalCount = toDump.getTotalCount()
                var prevCount = 0
                val reporter = object : DumpProgressReporter {
                    override var progress: Int = 0
                        set(value) {
                            field = value
                            toast.progress = (prevCount + value.toFloat()) / totalCount
                        }
                }
                toDump.forEach { (dumper, type) ->
                    toast.title = dumper.getTitle()
                    val outputs = mutableListOf<DumpOutput>()
                    if (type.data) {
                        toast.subtitle = dumper.getSubtitleData()
                        outputs += dumper.dumpData(reporter)
                        prevCount += reporter.progress
                    }
                    if (type.amounts) {
                        toast.progress = (prevCount + 1F) / totalCount
                        toast.subtitle = dumper.getSubtitleAmounts()
                        dumper.dumpAmounts()?.also { outputs += it }
                        prevCount++
                    }
                    Minecraft.getInstance().deferTask {
                        for (msg in CraftDumperCommand.createSuccessMessage(dumper, outputs, true))
                            source.sendFeedback(msg, true)
                    }
                }
                Minecraft.getInstance().deferTask { toast.hide() }
            }
        }
    }

    override val outputDirectory: File
        get() = Minecraft.getInstance().gameDir.resolve(ID)
}