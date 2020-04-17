@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.client
import net.pearx.craftdumper.common.helper.eachStack
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.stackCount
import net.pearx.craftdumper.common.helper.toFullString

val DumperArmor = dumperTable {
    registryName = craftdumper("armor")
    header = listOfNotNull("Item", client("Material"), "Slot", "Damage Reduce Amount", "Toughness", "Repair Material")
    amounts {
        eachArmor { item, _ ->
            +item.registryName
        }
    }
    count { armorCount() }
    table {
        eachArmor { item, stack ->
            row(header.size) {
                with(item) {
                    add { stack.toFullString() }
                    client { add { armorMaterial.getName() } }
                    add { equipmentSlot.getName() }
                    add { damageReduceAmount.toString() }
                    add { toughness.toString() }
                    add { armorMaterial.repairMaterial.toFullString() }
                }
            }
        }
    }
}

private fun armorCount() = stackCount<ItemArmor>()
private inline fun eachArmor(block: (item: ItemArmor, stack: ItemStack) -> Unit) = eachStack(block)
