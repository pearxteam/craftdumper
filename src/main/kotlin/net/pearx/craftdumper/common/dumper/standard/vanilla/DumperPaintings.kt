@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperPaintings = dumperTable {
    registryName = craftdumper("paintings")
    header = listOf("ID", "Width", "Height")
    amounts { +ForgeRegistries.PAINTING_TYPES.keys }
    count { ForgeRegistries.PAINTING_TYPES.count() }
    table {
        ForgeRegistries.PAINTING_TYPES.forEach { painting ->
            row(header.size) {
                with(painting) {
                    add { registryName.toString() }
                    add { width.toString() }
                    add { height.toString() }
                }
            }
        }
    }
}