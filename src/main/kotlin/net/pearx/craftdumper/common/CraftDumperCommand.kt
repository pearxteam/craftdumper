package net.pearx.craftdumper.common

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.common.dumper.*

class CraftDumperCommand : CommandBase() {
    companion object {
        private val DEFAULT_DUMP_TYPE = DumpType.FULL

        private fun dumpTypeOf(value: String) = DumpType.values().firstOrNull { it.value == value }
        private val dumpTypes = DumpType.values().map { it.value }
    }

    enum class DumpType(val value: String, val data: Boolean = false, val amounts: Boolean = false) {
        FULL("full", data = true, amounts = true),
        AMOUNTS("amounts", amounts = true),
        DATA("data", data = true);
    }

    override fun getName() = "craftdumper"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty()) throw createWrongUsageException(sender)

        val dumperName = args[0]
        val dumpType = (if (args.size > 1) dumpTypeOf(args[1]) else DEFAULT_DUMP_TYPE)
            ?: throw createWrongUsageException(sender)

        if (dumperName == "all") {
            for (dmpr in DumperRegistry)
                CraftDumper.proxy.createDump(this, sender, dmpr, dumpType)
        }
        else {
            val dumpers = lookupDumperRegistry(dumperName)
            if (dumpers.size != 1)
                throw createWrongUsageException(sender)

            CraftDumper.proxy.createDump(this, sender, dumpers[0], dumpType)
        }
    }

    fun createSuccessMessage(dumper: Dumper, outputs: List<DumpOutput>, displayOutputs: Boolean): List<ITextComponent> {
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
                val comma = TextComponentString(", ")
                first.appendSibling(comma)
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                comma.style.setParentStyle(null)
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
            2 -> getListOfStringsMatchingLastWord(args, dumpTypes)
            else -> listOf()
        }
    }

    private fun createWrongUsageException(sender: ICommandSender) = WrongUsageException(getUsage(sender), dumpTypes.joinToString("|"), getDumperNames().joinToString(", "))
}