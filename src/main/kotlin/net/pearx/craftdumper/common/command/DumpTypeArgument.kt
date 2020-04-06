package net.pearx.craftdumper.common.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.ISuggestionProvider
import net.minecraft.util.text.TranslationTextComponent
import net.pearx.craftdumper.common.dumper.Dumper
import java.util.concurrent.CompletableFuture

fun CommandContext<out Any?>.dumpType(name: String) = getArgument(name, DumpType::class.java)
val DUMP_TYPE_UNKNOWN = DynamicCommandExceptionType { obj -> TranslationTextComponent("dump_type.unknown", obj) }

object DumpTypeArgument : ArgumentType<DumpType> {
    override fun parse(reader: StringReader): DumpType {
        val name = reader.readUnquotedString()
        return dumpTypeOf(name) ?: throw DUMP_TYPE_UNKNOWN.create(name)
    }

    override fun <S : Any?> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return ISuggestionProvider.suggest(dumpTypes, builder)
    }

    override fun getExamples(): Collection<String> = DumpType.values().map { it.value }
}