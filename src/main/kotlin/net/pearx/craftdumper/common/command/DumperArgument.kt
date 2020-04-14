package net.pearx.craftdumper.common.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.ISuggestionProvider
import net.minecraft.command.arguments.ArgumentSerializer
import net.minecraft.command.arguments.ArgumentTypes
import net.minecraft.util.text.TranslationTextComponent
import net.pearx.craftdumper.common.dumper.Dumper
import net.pearx.craftdumper.common.dumper.getDumperNames
import net.pearx.craftdumper.common.dumper.lookupDumperRegistry
import net.pearx.craftdumper.common.helper.internal.craftdumper
import java.util.concurrent.CompletableFuture

fun CommandContext<out Any?>.dumper(name: String) = getArgument(name, Dumper::class.java)
val DUMPER_UNKNOWN = DynamicCommandExceptionType { obj -> TranslationTextComponent("dumper.unknown", obj) }

object DumperArgument : ArgumentType<Dumper> {
    private val EXAMPLES = listOf("blocks", "entities")

    override fun parse(reader: StringReader): Dumper {
        val s = reader.readUnquotedString()
        val dumpers = lookupDumperRegistry(s)
        if (dumpers.size != 1)
            throw DUMPER_UNKNOWN.create(s)
        else
            return dumpers[0]
    }

    override fun getExamples(): Collection<String> = EXAMPLES

    override fun <S : Any> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return ISuggestionProvider.suggest(getDumperNames().toMutableList().apply { add("all") }, builder)
    }

    fun register() {
        ArgumentTypes.register(craftdumper("dumper").toString(), DumperArgument::class.java, ArgumentSerializer { this })
    }
}