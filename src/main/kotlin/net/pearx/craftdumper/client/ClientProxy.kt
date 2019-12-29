package net.pearx.craftdumper.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.command.ICommandSender
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftdumper.CommonProxy
import net.pearx.craftdumper.CraftDumperCommand
import net.pearx.craftdumper.dumper.DumpOutput
import net.pearx.craftdumper.dumper.DumpProgressReporter
import net.pearx.craftdumper.dumper.Dumper

@SideOnly(Side.CLIENT)
class ClientProxy : CommonProxy {
    override fun createDump(command: CraftDumperCommand, sender: ICommandSender, dumper: Dumper, type: CraftDumperCommand.DumpType) {
        with(command) {
            Minecraft.getMinecraft().addScheduledTask {
                val toast = DumperToast(-1, dumper, "data")
                val reporter = object : DumpProgressReporter {
                    override var progress: Float
                        get() = toast.progress
                        set(value) {
                            toast.progress = value
                        }
                }
                Minecraft.getMinecraft().toastGui.add(toast)
                GlobalScope.launch {
                    val outputs = mutableListOf<DumpOutput>()
                    if (type.data)
                        outputs += dumper.dumpData(reporter)
                    if (type.amounts) {
                        toast.progress = 1F
                        toast.subtitle = "amounts"
                        dumper.dumpAmounts()?.also { outputs += it }
                    }
                    Minecraft.getMinecraft().addScheduledTask {
                        toast.hide()
                        for (msg in createSuccessMessage(dumper, outputs, true))
                            sender.sendMessage(msg)
                    }
                }
            }
        }
    }
}