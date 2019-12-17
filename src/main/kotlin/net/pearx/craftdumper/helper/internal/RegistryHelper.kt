package net.pearx.craftdumper.helper.internal

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.pearx.craftdumper.ID

internal fun <T : IForgeRegistryEntry<T>> IForgeRegistry<T>.lookupRegistryElements(name: String): List<T> {
    val foundElements = mutableListOf<T>()
    for (element in this) {
        if (element.registryName != null) {
            if (element.registryName.toString() == name)
                return listOf(element)
            if (element.registryName!!.path == name)
                foundElements.add(element)
        }
    }
    return foundElements
}

internal fun <T : IForgeRegistryEntry<T>> IForgeRegistry<T>.getRegistryElementNames(): List<String> {
    val names = ArrayList<String>(entries.size)
        for (element in this) {

            if (element.registryName != null)
                names += getRegistryElementName(element.registryName!!)
        }
    return names
}

internal fun <T : IForgeRegistryEntry<T>> IForgeRegistry<T>.getRegistryElementName(name: ResourceLocation): String {
    val found: Boolean = run {
        for (anotherElement in this@getRegistryElementName) {
            if (anotherElement.registryName != name &&
                anotherElement.registryName!!.path == name.path) {
                return@run true
            }
        }
        return@run false
    }

    return if (found)
        name.toString()
    else
        name.path
}