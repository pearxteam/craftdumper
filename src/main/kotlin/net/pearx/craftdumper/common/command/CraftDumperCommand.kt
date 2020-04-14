package net.pearx.craftdumper.common.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.common.dumper.DumpOutput
import net.pearx.craftdumper.common.dumper.Dumper
import net.pearx.craftdumper.common.dumper.DumperRegistry
import net.pearx.craftdumper.common.helper.internal.getTotalCount


object CraftDumperCommand {
    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher.register(
            Commands.literal("craftdumper").then(
                Commands.argument("dumper", DumperArgument).then(Commands.argument("type", DumpTypeArgument).executes { cmd ->
                    execute(cmd.source, listOf(cmd.dumper("dumper")), cmd.dumpType("type"))
                }).executes { cmd ->
                    execute(cmd.source, listOf(cmd.dumper("dumper")), DEFAULT_DUMP_TYPE)
                }
            ).then(
                Commands.literal("all").then(Commands.argument("type", DumpTypeArgument).executes { cmd ->
                    execute(cmd.source, DumperRegistry.values, cmd.dumpType("type"))
                }).executes { cmd ->
                    execute(cmd.source, DumperRegistry.values, DEFAULT_DUMP_TYPE)
                }
            )
        )
    }

    private fun execute(source: CommandSource, dumpers: Collection<Dumper>, dumpType: DumpType): Int {
        val mapped = dumpers.map { it to dumpType }
        CraftDumper.proxy.createDump(source, mapped)
        return mapped.getTotalCount() // todo
    }

    fun createSuccessMessage(dumper: Dumper, outputs: List<DumpOutput>, displayOutputs: Boolean): List<ITextComponent> {
        val lst = mutableListOf(TranslationTextComponent("commands.craftdumper.success", dumper.getTitle().apply { style.color = TextFormatting.GOLD }))

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
                first.appendSibling(StringTextComponent(", ").apply {
                    with(style) {
                        underlined = false
                        color = TextFormatting.RESET
                        clickEvent = null
                    }
                })
                first.appendSibling(comp)
            }
            lst += TranslationTextComponent("commands.craftdumper.outputs", first)
        }

        return lst
    }
}