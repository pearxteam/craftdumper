package net.pearx.craftdumper.common.helper

import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry

fun <V : IForgeRegistryEntry<V>> IForgeRegistry<V>.registerNonNull(v: V?) {
    if (v != null)
        register(v)
}

val isClient = FMLCommonHandler.instance().side == Side.CLIENT

fun <T> ifOrNull(bool: Boolean, value: T) = if (bool) value else null
inline fun <T> ifOrNull(bool: Boolean, func: () -> T) = if (bool) func() else null

fun <T> client(value: T) = ifOrNull(isClient, value)
inline fun <T> client(func: () -> T) = ifOrNull(isClient, func)

inline fun <T> buildMultilineString(iterable: Iterable<T>, crossinline action: StringBuilder.(T) -> Unit) = buildMultilineString_(iterable, action, Iterable<T>::forEach)

inline fun buildMultilineString(iterable: IntArray, crossinline action: StringBuilder.(Int) -> Unit) = buildMultilineString_(iterable, action, IntArray::forEach)

@PublishedApi
internal inline fun <I, E> buildMultilineString_(iterable: I, crossinline action: StringBuilder.(E) -> Unit, itr: I.((E) -> Unit) -> Unit): String {
    return buildString {
        var start = true
        iterable.itr { element ->
            if (start)
                start = false
            else
                appendln()
            action(element)
        }
    }
}