package net.pearx.craftdumper

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.event.ClickEvent
import net.pearx.craftdumper.dumper.*

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
        val reporter = object : DumpProgressReporter {
            override var progress: Double = 0.0
                get() = field
                set(value) { field = value }

        }
        val outputs = dumper.dumpContents(reporter)
        sender.sendMessage(TextComponentTranslation("commands.craftdumper.success", "${TextFormatting.GOLD}${dumper.registryName?.path}${TextFormatting.RESET}").apply {
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
        })
    }

    override fun getUsage(sender: ICommandSender) = "commands.craftdumper.usage"

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<String>, targetPos: BlockPos?): List<String> {
        return when (args.size) {
            1 -> CommandBase.getListOfStringsMatchingLastWord(args, getDumperNames().toMutableList().apply { this += "all" })
            else -> listOf()
        }
    }

    private fun createWrongUsageException(sender: ICommandSender) = WrongUsageException(getUsage(sender), getDumperNames().joinToString(", "))
}