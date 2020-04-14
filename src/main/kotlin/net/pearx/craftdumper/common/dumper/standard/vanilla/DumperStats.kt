@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.client
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperStats = dumperTable {
    registryName = craftdumper("stats")
    header = listOfNotNull("ID", client("Translation Key"))
    amounts { +ForgeRegistries.STAT_TYPES.keys }
    count { ForgeRegistries.STAT_TYPES.count() }
    table {
        ForgeRegistries.STAT_TYPES.forEach { stat ->
            row(header.size) {
                with(stat) {
                    add { registryName.toString() }
                    client { add { translationKey } }
                }
            }
        }
    }
}