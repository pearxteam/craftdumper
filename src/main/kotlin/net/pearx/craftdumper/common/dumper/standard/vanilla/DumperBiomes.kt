@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.entity.EntityClassification
import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.client
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.internal.mapArray
import net.pearx.craftdumper.common.helper.mutableListOfNotNull
import net.pearx.craftdumper.common.helper.toHexColorString
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperBiomes = dumperTable {
    registryName = craftdumper("biomes")
    header = mutableListOfNotNull(
        "ID",
        client("Name"),
        "Parent Biome",
        "Category",
        "Depth",
        "Scale",
        "Default Temperature",
        "Temperature Category",
        "Precipitation",
        "Downfall",
        "Is High Humidity",
        "Water Color",
        "Water Fog Color",
        "Top Block",
        "Under Block",
        "River Biome",
        "Spawning Chance",
        *enumValues<EntityClassification>().mapArray { classification -> "${classification.getName().capitalize()} Spawn List: Entity*(Min Group-Max Group):Weight" }
    )
    amounts { +ForgeRegistries.BIOMES.keys }
    count { ForgeRegistries.BIOMES.count() }
    table {
        ForgeRegistries.BIOMES.forEach { biome ->
            row(header.size) {
                with(biome) {
                    add { registryName.toString() }
                    client { add { displayName.unformattedComponentText.toString() } }
                    add { parent.orEmpty() }
                    add { category.getName() }
                    add { depth.toString() }
                    add { scale.toString() }
                    add { defaultTemperature.toString() }
                    add { tempCategory.getName() }
                    add { precipitation.getName() }
                    add { downfall.toString() }
                    add { isHighHumidity.toPlusMinusString() }
                    add { waterColor.toHexColorString() }
                    add { waterFogColor.toHexColorString() }
                    add { surfaceBuilderConfig.top.toString() }
                    add { surfaceBuilderConfig.under.toString() }
                    add { river.registryName.toString() }
                    // todo carvers, decorators maybe somewhen?
                    add { spawningChance.toString() }
                    enumValues<EntityClassification>().forEach { type ->
                        add { getSpawns(type).joinToString(separator = System.lineSeparator()) }
                    }
                }
            }
        }
    }
}