@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.item.ItemFood
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.eachStack
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.stackCount
import net.pearx.craftdumper.common.helper.toFullString
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperFood = dumperTable {
    registryName = craftdumper("food")
    header = listOf("Item", "Heal Amount", "Saturation Modifier", "Is Wolfs Favorite Meal", "Is Always Edible", "Item Use Duration", "Potion Effect", "Potion Effect Probability")
    amounts {
        eachStack<ItemFood> { item, _ ->
            this += item.registryName
        }
    }
    count { stackCount<ItemFood>() }
    table {
        eachStack<ItemFood> { item, stack ->
            row(header.size) {
                with(item) {
                    add { stack.toFullString() }
                    add { getHealAmount(stack).toString() }
                    add { getSaturationModifier(stack).toString() }
                    add { isWolfsFavoriteMeat.toPlusMinusString() }
                    add { alwaysEdible.toPlusMinusString() }
                    add { itemUseDuration.toString() }
                    if (potionId != null) {
                        add { potionId.toString() }
                        add { potionEffectProbability.toString() }
                    }
                    else repeat(2) { add("") }
                }
            }
        }
    }
}