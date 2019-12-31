package net.pearx.craftdumper.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.command.ICommandSender
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftdumper.common.CommonProxy
import net.pearx.craftdumper.common.CraftDumperCommand
import net.pearx.craftdumper.common.dumper.DumpOutput
import net.pearx.craftdumper.common.dumper.DumpProgressReporter
import net.pearx.craftdumper.common.dumper.Dumper

@SideOnly(Side.CLIENT)
class ClientProxy : CommonProxy {
    override fun createDump(command: CraftDumperCommand, sender: ICommandSender, toDump: List<Pair<Dumper, CraftDumperCommand.DumpType>>) {
        with(command) {
            Minecraft.getMinecraft().addScheduledTask {
                val toast = DumperToast(-1)
                val totalProgress = toDump.size.toFloat()
                Minecraft.getMinecraft().toastGui.add(toast)
                GlobalScope.launch {
                    toDump.forEachIndexed { index, (dumper, type) ->
                        val reporter = object : DumpProgressReporter {
                            override var progress: Float = 0F
                                set(value) {
                                    field = value
                                    toast.progress = (index + value) / totalProgress
                                }
                        }
                        toast.title = dumper.getTitle()
                        val outputs = mutableListOf<DumpOutput>()
                        if (type.data) {
                            toast.subtitle = dumper.getSubtitleData()
                            outputs += dumper.dumpData(reporter)
                        }
                        if (type.amounts) {
                            toast.progress = (index + 1F) / totalProgress
                            toast.subtitle = dumper.getSubtitleAmounts()
                            dumper.dumpAmounts()?.also { outputs += it }
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