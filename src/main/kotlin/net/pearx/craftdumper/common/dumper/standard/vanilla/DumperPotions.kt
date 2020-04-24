@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperPotions = dumperTable {
    registryName = craftdumper("potions")
    header = listOf("ID")
    amounts { +ForgeRegistries.POTION_TYPES.keys }
    count { ForgeRegistries.POTION_TYPES.count() }
    table {
        data {
            ForgeRegistries.POTION_TYPES.forEach { potion ->
                row {
                    with(potion) {
                        add { registryName.toString() }
                        add { effects.joinToString(System.lineSeparator()) }
                    }
                }
            }
        }
    }
}