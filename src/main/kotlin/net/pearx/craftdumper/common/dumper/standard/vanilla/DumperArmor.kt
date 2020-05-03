@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.*
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperArmor = dumperTable {
    registryName = craftdumper("armor")
    header = listOfNotNull("Item", client("Material"), "Slot", "Damage Reduce Amount", "Toughness", "Durability", "Enchantability", "Repair Material")
    amounts {
        eachArmor { item, _ ->
            +item.registryName
        }
    }
    count { armorCount() }
    table {
        data {
            eachArmor { item, stack ->
                row {
                    with(item) {
                        add { stack.toFullString() }
                        client { add { armorMaterial.name } }
                        add { equipmentSlot.getName() }
                        add { damageReduceAmount.toString() }
                        add { toughness.toString() }
                        add { stack.getDurabilityString() }
                        add { getItemEnchantability(stack).toString() }
                        add { armorMaterial.repairMaterial.toFullString() }
                    }
                }
            }
        }
    }
}

private fun armorCount() = stackCount { it.food != null }
private inline fun eachArmor(block: (item: ArmorItem, stack: ItemStack) -> Unit) = eachStack({ it is ArmorItem }) { item, stack -> block(item as ArmorItem, stack) }