@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.client
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperParticles = dumperTable {
    registryName = craftdumper("particles")
    header = listOfNotNull("ID", client("Always Show"), "Deserializer Class")
    amounts { +ForgeRegistries.PARTICLE_TYPES.keys }
    count { ForgeRegistries.PARTICLE_TYPES.count() }
    table {
        data {
            ForgeRegistries.PARTICLE_TYPES.forEach { painting ->
                row {
                    with(painting) {
                        add { registryName.toString() }
                        client { add { alwaysShow.toPlusMinusString() } }
                        add { deserializer::class.java.name }
                    }
                }
            }
        }
    }
}