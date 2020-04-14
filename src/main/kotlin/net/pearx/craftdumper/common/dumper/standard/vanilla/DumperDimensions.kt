@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.world.dimension.DimensionType
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperDimensions = dumperTable {
    registryName = craftdumper("dimensions")
    header = listOf("ID", "Numeric ID", "Suffix", "Directory", "Has Sky Light", "Is Vanilla")
    amounts { DimensionType.getAll().forEach { +it.registryName } }
    count { DimensionType.getAll().count() }
    table {
        DimensionType.getAll().forEach { dim ->
            row(header.size) {
                with(dim) {
                    add { registryName.toString() }
                    add { id.toString() }
                    add { suffix }
                    add { directory }
                    add { hasSkyLight().toPlusMinusString() }
                    add { isVanilla.toPlusMinusString() }
                }
            }
        }
    }
}