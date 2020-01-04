package net.pearx.craftdumper.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.command.ICommandSender
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftdumper.common.CommonProxy
import net.pearx.craftdumper.common.CraftDumperCommand
import net.pearx.craftdumper.common.dumper.DumpOutput
import net.pearx.craftdumper.common.dumper.DumpProgressReporter
import net.pearx.craftdumper.common.dumper.Dumper
import net.pearx.craftdumper.common.helper.internal.getTotalCount

@SideOnly(Side.CLIENT)
class ClientProxy : CommonProxy {
    override fun createDump(command: CraftDumperCommand, sender: ICommandSender, toDump: List<Pair<Dumper, CraftDumperCommand.DumpType>>) {
        with(command) {
            Minecraft.getMinecraft().addScheduledTask {
                val toast = DumperToast(-1)
                Minecraft.getMinecraft().toastGui.add(toast)
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
                        Minecraft.getMinecraft().addScheduledTask {
                            for (msg in createSuccessMessage(dumper, outputs, true))
                                sender.sendMessage(msg)
                        }
                    }
                    Minecraft.getMinecraft().addScheduledTask { toast.hide() }
                }
            }
        }
    }
}