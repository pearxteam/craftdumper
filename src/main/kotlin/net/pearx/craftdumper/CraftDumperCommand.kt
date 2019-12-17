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
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.WorldServer
import net.pearx.craftdumper.client.DumperToast
import net.pearx.craftdumper.dumper.*
import net.pearx.craftdumper.helper.isClient

class CraftDumperCommand : CommandBase() {
    override fun getName() = "craftdumper"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty()) {
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
                        for (msg in createSuccessMessage(dumper, outputs, true))
                            Minecraft.getMinecraft().player.sendMessage(msg)
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
                    for (msg in createSuccessMessage(dumper, outputs, false))
                        sender.sendMessage(msg)
                }
            }
        }
    }

    private fun createStartMessage(dumper: Dumper): ITextComponent = TextComponentTranslation("commands.craftdumper.start", dumper.getTextComponent())

    private fun createSuccessMessage(dumper: Dumper, outputs: List<DumpOutput>, displayOutputs: Boolean): List<ITextComponent> {
        val lst = mutableListOf(TextComponentTranslation("commands.craftdumper.success", dumper.getTextComponent().apply { style.color = TextFormatting.GOLD }))

        if (displayOutputs && outputs.isNotEmpty()) {
            val outputComponents = outputs.map {
                it.getTextComponent().apply {
                    with(style) {
                        underlined = true
                        color = TextFormatting.BLUE
                    }
                }
            }
            val first: ITextComponent = outputComponents[0]
            for (comp in outputComponents.subList(1, outputComponents.size)) {
                first.appendText(", ")
                first.appendSibling(comp)
            }
            lst += TextComponentTranslation("commands.craftdumper.outputs", first)
        }

        return lst
    }

    override fun getUsage(sender: ICommandSender) = "commands.craftdumper.usage"

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<String>, targetPos: BlockPos?): List<String> {
        return when (args.size) {
            1 -> getListOfStringsMatchingLastWord(args, getDumperNames().toMutableList().also { it += "all" })
            else -> listOf()
        }
    }

    private fun createWrongUsageException(sender: ICommandSender) = WrongUsageException(getUsage(sender), getDumperNames().joinToString(", "))
}