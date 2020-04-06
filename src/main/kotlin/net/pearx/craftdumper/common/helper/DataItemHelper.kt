package net.pearx.craftdumper.common.helper

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.NonNullList
import net.minecraftforge.registries.ForgeRegistries

fun ItemStack.appendTo(to: Appendable) {
    to.apply {
        if (isEmpty) return

        append(item.registryName.toString())

        if (hasTag()) {
            append(' ')
            append(tag.toString())
        }

        if (count != 1) {
            append(" x")
            append(count.toString())
        }
    }
}

fun ItemStack.toFullString() = buildString { appendTo(this) }

//inline fun <reified T : Item> eachStack(block: (item: T, stack: ItemStack) -> Unit) {
//    for (item in ForgeRegistries.ITEMS) {
//        if (item is T) {
//            val stacks = NonNullList.create<ItemStack>().also { item.getSubItems(it) }
//            for (stack in stacks) {
//                block(item, stack)
//            }
//        }
//    }
//}
//
//inline fun <reified T : Item> stackCount(): Int {
//    var count = 0
//    for(item in ForgeRegistries.ITEMS) {
//        if(item is T) {
//            count += NonNullList.create<ItemStack>().also { item.getSubItems(it) }.size
//        }
//    }
//    return count
//}
//
//fun Item.getSubItems(list: NonNullList<ItemStack>) {
//    getSubItems(creativeTab ?: CreativeTabs.SEARCH, list)
//}
//
//fun Ingredient.appendTo(to: Appendable) {
//    val matchingStacksPublic = getMatchingStacks()
//    if (matchingStacksPublic.isNotEmpty()) {
//        if (this is OreIngredient) {
//            to.append("ore:")
//            to.append(run {
//                val ores = readField<NonNullList<ItemStack>>("ores")
//                for (oreId in OreDictionary.getOreIDs(matchingStacksPublic[0])) {
//                    val oreName = OreDictionary.getOreName(oreId)
//                    val ores1 = OreDictionary.getOres(oreName)
//                    if (ores == ores1)
//                        return@run oreName
//                }
//                return@run ""
//            })
//        }
//        else {
//            val matchingStacksInternal = matchingStacks
//            if (matchingStacksInternal.isNotEmpty())
//                appendStackListOrSeparatedTo(to, matchingStacksInternal)
//            else
//                appendStackListOrSeparatedTo(to, matchingStacksPublic)
//        }
//    }
//}
//
//private fun appendStackListOrSeparatedTo(to: Appendable, stacks: Array<ItemStack>) {
//    var startStacks = true
//    for (stack in stacks) {
//        if (startStacks)
//            startStacks = false
//        else
//            to.append(" | ")
//        to.append(stack.toFullString(true))
//    }
//}
//
//fun Ingredient.toFullString() = buildString { appendTo(this) }