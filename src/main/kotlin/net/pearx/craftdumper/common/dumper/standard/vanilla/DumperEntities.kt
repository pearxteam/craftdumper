@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toHexColorString

val DumperEntities = dumperTable {
    registryName = craftdumper("entities")
    header = listOf("ID", "Name", "Class Name", "Primary Egg Color", "Secondary Egg Color")
    amounts { this += ForgeRegistries.ENTITIES.keys }
    count { ForgeRegistries.ENTITIES.count() }
    table {
        ForgeRegistries.ENTITIES.forEach { entity ->
            row(header.size) {
                with(entity) {
                    add { registryName.toString() }
                    add { name }
                    add { entityClass.name }
                    add { egg?.primaryColor?.toHexColorString().orEmpty() }
                    add { egg?.secondaryColor?.toHexColorString().orEmpty() }
                }
            }
        }
    }
}