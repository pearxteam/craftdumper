@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperBlocks = dumperTable {
    registryName = craftdumper("blocks")
    header = listOf("ID", "Class Name", "BlockState Properties", "BlockState Class Name")
    amounts { +ForgeRegistries.BLOCKS.keys }
    count { ForgeRegistries.BLOCKS.count() }
    table {
        ForgeRegistries.BLOCKS.forEach { block ->
            row(header.size) {
                with(block) {
                    add { registryName.toString() }
                    add { this::class.java.name }
                    add { blockState.properties.joinToString(separator = System.lineSeparator()) }
                    add { blockState::class.java.name }
                }
            }
        }
    }
}