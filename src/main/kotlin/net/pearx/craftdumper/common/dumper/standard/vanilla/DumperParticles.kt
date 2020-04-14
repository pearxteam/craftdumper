@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.client
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperParticles = dumperTable {
    registryName = craftdumper("particles")
    header = listOfNotNull("ID", client("Always Show"), "Deserializer Class")
    amounts { +ForgeRegistries.PARTICLE_TYPES.keys }
    count { ForgeRegistries.PARTICLE_TYPES.count() }
    table {
        ForgeRegistries.PARTICLE_TYPES.forEach { painting ->
            row(header.size) {
                with(painting) {
                    add { registryName.toString() }
                    client { add { alwaysShow.toPlusMinusString() } }
                    add { deserializer::class.java.name }
                }
            }
        }
    }
}