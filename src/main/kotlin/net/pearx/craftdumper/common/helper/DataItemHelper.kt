package net.pearx.craftdumper.common.helper

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.NonNullList
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.OreIngredient

fun ItemStack.appendTo(to: Appendable, wildcardMetaAsAny: Boolean = false) {
    to.apply {
        if (isEmpty) return

        append(item.registryName.toString())

        if (metadata != 0) {
            append(':')
            if (wildcardMetaAsAny && metadata == OreDictionary.WILDCARD_VALUE)
                append('*')
            else
                append(metadata.toString())
        }

        if (hasTagCompound()) {
            append(' ')
            append(tagCompound.toString())
        }

        if (count != 1) {
            append(" x")
            append(count.toString())
        }
    }
}

fun ItemStack.toFullString(wildcardMetaAsAny: Boolean = false) = buildString { appendTo(this, wildcardMetaAsAny) }

inline fun <reified T : Item> eachStack(block: (item: T, stack: ItemStack) -> Unit) {
    for (item in ForgeRegistries.ITEMS) {
        if (item is T) {
            val stacks = NonNullList.create<ItemStack>().also { item.getSubItems(it) }
            for (stack in stacks) {
                block(item, stack)
            }
        }
    }
}

inline fun <reified T : Item> stackCount(): Int {
    var count = 0
    for(item in ForgeRegistries.ITEMS) {
        if(item is T) {
            count += NonNullList.create<ItemStack>().also { item.getSubItems(it) }.size
        }
    }
    return count
}

fun Item.getSubItems(list: NonNullList<ItemStack>) {
    getSubItems(creativeTab ?: CreativeTabs.SEARCH, list)
}

fun Ingredient.appendTo(to: Appendable) {
    val matchingStacksPublic = getMatchingStacks()
    if (matchingStacksPublic.isNotEmpty()) {
        if (this is OreIngredient) {
            to.append("ore:")
            to.append(run {
                val ores = readField<NonNullList<ItemStack>>("ores")
                for (oreId in OreDictionary.getOreIDs(matchingStacksPublic[0])) {
                    val oreName = OreDictionary.getOreName(oreId)
                    val ores1 = OreDictionary.getOres(oreName)
                    if (ores == ores1)
                        return@run oreName
                }
                return@run ""
            })
        }
        else {
            val matchingStacksInternal = matchingStacks
            if (!matchingStacksInternal.isEmpty())
                appendStackListOrSeparatedTo(to, matchingStacksInternal)
            else
                appendStackListOrSeparatedTo(to, matchingStacksPublic)
        }
    }
}

private fun appendStackListOrSeparatedTo(to: Appendable, stacks: Array<ItemStack>) {
    var startStacks = true
    for (stack in stacks) {
        if (startStacks)
            startStacks = false
        else
            to.append(" | ")
        to.append(stack.toFullString(true))
    }
}

fun Ingredient.toFullString() = buildString { appendTo(this) }