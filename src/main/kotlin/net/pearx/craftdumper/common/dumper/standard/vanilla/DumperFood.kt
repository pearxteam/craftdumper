@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.*
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperFood = dumperTable {
    registryName = craftdumper("food")
    header = listOf("Item", "Healing", "Saturation", "Is Meat", "Can Eat When Full", "Is Fast Eating", "Use Duration", "Effects")
    amounts {
        eachFood { item, _ ->
            +item.registryName
        }
    }
    count { foodCount() }
    table {
        data {
            eachFood { item, stack ->
                row {
                    with(item) {
                        add { stack.toFullString() }
                        with(food!!) {
                            add { healing.toString() }
                            add { saturation.toString() }
                            add { isMeat.toPlusMinusString() }
                            add { canEatWhenFull().toPlusMinusString() }
                            add { isFastEating.toPlusMinusString() }
                            add { getUseDuration(stack).toString() }
                            add {
                                effects.joinToString(System.lineSeparator()) { (potion, probability) ->
                                    "${potion.effectName} - ${probability.toPercents()}%"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun foodCount() = stackCount { it.food != null }
private inline fun eachFood(block: (item: Item, stack: ItemStack) -> Unit) = eachStack({ it.food != null }, block)