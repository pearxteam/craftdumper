@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.buildMultilineString
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperEnchantments = dumperTable {
    registryName = craftdumper("enchantments")
    header = listOf("ID", "Name", "Class Name", "Levels", "Rarity", "Is Curse", "Type", "Allowed on Books", "Is Treasure", "Localized Name [Min Enchantability - Max Enchantability]")
    amounts { +ForgeRegistries.ENCHANTMENTS.keys }
    count { ForgeRegistries.ENCHANTMENTS.count() }
    table {
        ForgeRegistries.ENCHANTMENTS.forEach { enchantments ->
            row(header.size) {
                with(enchantments) {
                    add { registryName.toString() }
                    add { name }
                    add { this::class.java.name }
                    add { "$minLevel - $maxLevel" }
                    add { rarity.toString() }
                    add { isCurse.toPlusMinusString() }
                    add { type.toString() }
                    add { isAllowedOnBooks.toPlusMinusString() }
                    add { isTreasureEnchantment.toPlusMinusString() }
                    add {
                        buildMultilineString(minLevel..maxLevel) {
                            append(getTranslatedName(it))
                            append(" [")
                            append(getMinEnchantability(it))
                            append(" - ")
                            append(getMaxEnchantability(it))
                            append("]")
                        }
                    }
                }
            }
        }
    }
}