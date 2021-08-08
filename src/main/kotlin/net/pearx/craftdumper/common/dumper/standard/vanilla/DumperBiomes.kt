@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.client.resources.I18n
import net.minecraft.entity.EntityClassification
import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.*
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperBiomes = dumperTable {
    registryName = craftdumper("biomes")
    header = mutableListOfNotNull(
        "ID",
        client("Name"),
        "Category",
        "Depth",
        "Scale",
        "Default Temperature",
        "Temperature Modifier",
        "Precipitation",
        "Downfall",
        "Is High Humidity",
        client("Water Color"),
        client("Water Fog Color"),
        "Create Spawn Probability",
        "Valid Spawn Biome for Player",
        *enumValues<EntityClassification>().mapArray { classification -> "${classification.getName().replaceFirstChar(Char::uppercaseChar)} Spawn List: Entity*(Min Group-Max Group):Weight" }
    )
    amounts { +ForgeRegistries.BIOMES.keys }
    count { ForgeRegistries.BIOMES.count() }
    table {
        data {
            ForgeRegistries.BIOMES.forEach { biome ->
                row {
                    with(biome) {
                        add { registryName.toString() }
                        client { add { I18n.format("biome.${registryName!!.namespace}.${registryName!!.path}") } }
                        add { category.getName() }
                        add { depth.toString() }
                        add { scale.toString() }
                        add { temperature.toString() }
                        add { climate.temperatureModifier.getName() }
                        add { precipitation.getName() }
                        add { downfall.toString() }
                        add { isHighHumidity.toPlusMinusString() }
                        client { add { waterColor.toHexColorString() } }
                        client { add { waterFogColor.toHexColorString() } }
                        add { mobSpawnInfo.creatureSpawnProbability.toString() }
                        add { mobSpawnInfo.isValidSpawnBiomeForPlayer.toPlusMinusString() }
                        enumValues<EntityClassification>().forEach { type ->
                            add { mobSpawnInfo.getSpawners(type).joinToString(separator = System.lineSeparator()) }
                        }
                        // todo carvers, decorators maybe somewhen?
                    }
                }
            }
        }
    }
}