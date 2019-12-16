package net.pearx.craftdumper

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.world.WorldServer
import net.pearx.craftdumper.client.DumperToast
import net.pearx.craftdumper.dumper.*
import net.pearx.craftdumper.helper.isClient

class CraftDumperCommand : CommandBase() {
    override fun getName() = "craftdumper"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (args.size < 1) {
            throw createWrongUsageException(sender)
        }

        val dumperName = args[0]
        if (dumperName == "all") {
            for (dmpr in DumperRegistry)
                createDump(sender, dmpr)
        }
        else {
            val dumpers = lookupDumperRegistry(dumperName)
            if (dumpers.size != 1)
                throw createWrongUsageException(sender)

            createDump(sender, dumpers[0])
        }
    }

    private fun createDump(sender: ICommandSender, dumper: Dumper) {
        if (isClient) {
            Minecraft.getMinecraft().addScheduledTask {
                val toast = DumperToast(dumper, DumpOutputType.DATA)
                val reporter = object : DumpProgressReporter {
                    override var progress: Float
                        get() = toast.progress
                        set(value) {
                            toast.progress = value
                        }
                }
                Minecraft.getMinecraft().toastGui.add(toast)
                GlobalScope.launch {
                    val outputs = dumper.dumpData(reporter)
                    Minecraft.getMinecraft().addScheduledTask {
                        toast.hide()
                        Minecraft.getMinecraft().player.sendMessage(createFinishMessage(dumper, outputs, true))
                    }
                }
            }
        }
        else {
            // todo somehow display progress here
            val reporter = object : DumpProgressReporter {
                override var progress: Float = 0F
            }
            sender.sendMessage(createStartMessage(dumper))
            GlobalScope.launch {
                val outputs = dumper.dumpData(reporter)
                (sender.entityWorld as WorldServer).addScheduledTask {
                    sender.sendMessage(createFinishMessage(dumper, outputs, false))
                }
            }
        }
    }

    private fun createStartMessage(dumper: Dumper): ITextComponent = TextComponentTranslation("commands.craftdumper.start", "${TextFormatting.GOLD}${dumper.getDisplayName()}${TextFormatting.RESET}")

    private fun createFinishMessage(dumper: Dumper, outputs: List<DumpOutput>, displayOutputs: Boolean): ITextComponent {
        return TextComponentTranslation("commands.craftdumper.success${if(displayOutputs) "_outputs" else ""}", "${TextFormatting.GOLD}${dumper.getDisplayName()}${TextFormatting.RESET}").apply {
            if(displayOutputs) {
                var start = true
                for (output in outputs) {
                    if (start) {
                        start = false
                        appendText(" ")
                    }
                    else
                        appendText(", ")
                    val style = Style().setClickEvent(ClickEvent(ClickEvent.Action.OPEN_FILE, output.path.toString())).setUnderlined(true).setColor(TextFormatting.BLUE)
                    appendSibling(TextComponentTranslation(output.translationKey).setStyle(style))
                }
                appendText(".")
            }
        }
    }

    override fun getUsage(sender: ICommandSender) = "commands.craftdumper.usage"

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<String>, targetPos: BlockPos?): List<String> {
        return when (args.size) {
            1 -> getListOfStringsMatchingLastWord(args, getDumperNames().toMutableList().apply { this += "all" })
            else -> listOf()
        }
    }

    private fun createWrongUsageException(sender: ICommandSender) = WrongUsageException(getUsage(sender), getDumperNames().joinToString(", "))
}