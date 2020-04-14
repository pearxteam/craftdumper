package net.pearx.craftdumper.common.helper

import net.minecraft.world.server.ServerWorld
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.fml.server.ServerLifecycleHooks
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry

fun <V : IForgeRegistryEntry<V>> IForgeRegistry<V>.registerNonNull(v: V?) {
    if (v != null)
        register(v)
}

val isClient = FMLEnvironment.dist == Dist.CLIENT

val defaultWorld: ServerWorld
    get() = ServerLifecycleHooks.getCurrentServer().worlds.first()

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

inline fun buildMultilineString(iterable: IntArray, action: StringBuilder.(Int) -> Unit): String {
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

inline fun <T> buildMultilineString(iterable: Array<T>, action: StringBuilder.(T) -> Unit): String {
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