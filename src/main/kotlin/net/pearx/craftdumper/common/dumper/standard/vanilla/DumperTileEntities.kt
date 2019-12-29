@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperTileEntities = dumperTable {
    registryName = craftdumper("tile_entities")
    header = listOf("ID", "Class Name", "Is Tickable")
    amounts { this += TileEntity.REGISTRY.keys }
    count { TileEntity.REGISTRY.count() }
    table {
        TileEntity.REGISTRY.keys.forEach { id ->
            row(header.size) {
                add { id.toString() }
                val clazz = TileEntity.REGISTRY.getObject(id)!! // can't iterate through (key, clazz) pairs :(
                add { clazz.name }
                add { ITickable::class.java.isAssignableFrom(clazz).toPlusMinusString() }
            }
        }
    }
}