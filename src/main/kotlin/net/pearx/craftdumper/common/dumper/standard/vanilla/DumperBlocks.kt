@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperBlocks = dumperTable {
    registryName = craftdumper("blocks")
    header = listOf("ID", "Class Name", "Tags", "BlockState Properties", "BlockState Class Name")
    amounts { +ForgeRegistries.BLOCKS.keys }
    count { ForgeRegistries.BLOCKS.count() }
    table {
        data {
            ForgeRegistries.BLOCKS.forEach { block ->
                row {
                    with(block) {
                        add { registryName.toString() }
                        add { this::class.java.name }
                        add {
                            tags.joinToString(System.lineSeparator())
                        }
                        add { defaultState.properties.joinToString(separator = System.lineSeparator()) }
                        add { defaultState::class.java.name }
                    }
                }
            }
        }
    }
}