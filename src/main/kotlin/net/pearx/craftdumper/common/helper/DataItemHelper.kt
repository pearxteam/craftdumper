package net.pearx.craftdumper.common.helper

import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.tags.ITag
import net.minecraft.util.NonNullList
import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.CraftDumper

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

inline fun eachStack(itemStackFilter: (ItemStack) -> Boolean = { true }, itemFilter: (Item) -> Boolean = { true }, block: (item: Item, stack: ItemStack) -> Unit) {
    for (item in ForgeRegistries.ITEMS) {
        if (itemFilter(item)) {
            val stacks = NonNullList.create<ItemStack>().also { item.fillItemGroup(it) }
            for (stack in stacks) {
                if (itemStackFilter(stack)) {
                    block(item, stack)
                }
            }
        }
    }
}

inline fun eachItem(itemFilter: (Item) -> Boolean = { true }, block: (item: Item, stack: ItemStack) -> Unit) = eachStack(itemFilter = itemFilter, block = block)

inline fun countStacks(itemFilter: (Item) -> Boolean = { true }, itemStackFilter: (ItemStack) -> Boolean = { true }): Int {
    var count = 0
    for (item in ForgeRegistries.ITEMS) {
        if (itemFilter(item)) {
            for (stack in NonNullList.create<ItemStack>().also { item.fillItemGroup(it) })
                if (itemStackFilter(stack))
                    count++
        }
    }
    return count
}

inline fun countItems(itemFilter: (Item) -> Boolean = { true }): Int = countStacks(itemFilter = itemFilter)


fun Item.fillItemGroup(list: NonNullList<ItemStack>) {
    try {
        fillItemGroup(group ?: ItemGroup.SEARCH, list)
    } catch (e: Throwable) {
        CraftDumper.log.error("An error occurred while filling an ItemGroup!", e)
    }
}

fun Ingredient.appendTo(to: Appendable) {
    var startLists = true
    for (list in acceptedItems) {
        if (startLists)
            startLists = false
        else
            to.append(" | ")

        if (list is Ingredient.TagList && list.tag is ITag.INamedTag) {
            to.append("[")
            to.append((list.tag as ITag.INamedTag<Item>).name.toString())
            to.append("]")
        } else
            appendStackListOrSeparatedTo(to, list.stacks)
    }
}

private fun appendStackListOrSeparatedTo(to: Appendable, stacks: Collection<ItemStack>) {
    var startStacks = true
    for (stack in stacks) {
        if (startStacks)
            startStacks = false
        else
            to.append(" | ")
        to.append(stack.toFullString())
    }
}

fun Ingredient.toFullString() = buildString { appendTo(this) }

fun ItemStack.getDurabilityString() = if (isDamageable) maxDamage.toString() else "Infinite"