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

inline fun <T> buildMultilineString(iterable: Iterable<T>, action: StringBuilder.(T) -> Unit): String {
    return buildString {
        var start = true
        for (element in iterable) {
            if (start)
                start = false
            else
                appendln()
            action(element)
        }
    }
}