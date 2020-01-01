@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.fml.common.IWorldGenerator
import net.minecraftforge.fml.common.registry.GameRegistry
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.getOwnerModId
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.readField

val DumperWorldGenerators = dumperTable {
    registryName = craftdumper("world_generators")
    header = listOf("Mod ID", "Class Name", "Weight")
    amounts { registry().keys.forEach { this += it::class.getOwnerModId() } }
    count { registry().count() }
    table {
        registry().entries.forEach { (key, value) ->
            row(header.size) {
                add { key::class.getOwnerModId() }
                add { key::class.java.name }
                add { value.toString() }
            }
        }
    }
}

private fun registry() = GameRegistry::class.readField<Map<IWorldGenerator, Int>>("worldGeneratorIndex")