@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.tileentity.ITickableTileEntity
import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperTileEntities = dumperTable {
    registryName = craftdumper("tile_entities")
    header = listOf("ID", "Class Name", "Valid Blocks", "Datafixer Type", "Is Tickable")
    amounts { +ForgeRegistries.TILE_ENTITIES.keys }
    count { ForgeRegistries.TILE_ENTITIES.count() }
    table {
        ForgeRegistries.TILE_ENTITIES.forEach { tile ->
            row(header.size) {
                add { tile.registryName.toString() }
                val clazz = tile.create()?.let { it::class.java }
                if (clazz == null)
                    repeat(4) { add("") }
                else {
                    add { clazz.name }
                    add { tile.validBlocks.joinToString(System.lineSeparator()) { it.registryName.toString() } }
                    add { tile.datafixerType?.toString().orEmpty() }
                    add { ITickableTileEntity::class.java.isAssignableFrom(clazz).toPlusMinusString() }
                }
            }
        }
    }
}