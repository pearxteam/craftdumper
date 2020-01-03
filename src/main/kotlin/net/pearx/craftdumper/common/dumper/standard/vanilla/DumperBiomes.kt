@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.entity.EnumCreatureType
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.client
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.mutableListOfNotNull
import net.pearx.craftdumper.common.helper.toHexColorString
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperBiomes = dumperTable {
    registryName = craftdumper("biomes")
    header = mutableListOfNotNull("ID", client("Name"), "Default Temperature", "Base Height", "Height Variation", "Class Name", "Is Snowy", "Can Rain", "Rainfall", "Base Biome", "Filler Block", "Top Block", client("Water Color"), "Water Color Multiplier", "Creature Spawning Chance").also {
        for (type in EnumCreatureType.values())
            it += "${type.toString().toLowerCase().capitalize()} Spawn List: Entity*(Min Group-Max Group):Weight"
    }
    amounts { +ForgeRegistries.BIOMES.keys }
    count { ForgeRegistries.BIOMES.count() }
    table {
        ForgeRegistries.BIOMES.forEach { biome ->
            row(header.size) {
                with(biome) {
                    add { registryName.toString() }
                    client { add { biomeName } }
                    add { defaultTemperature.toString() }
                    add { baseHeight.toString() }
                    add { heightVariation.toString() }
                    add { this::class.java.name }
                    add { isSnowyBiome.toPlusMinusString() }
                    add { canRain().toPlusMinusString() }
                    add { rainfall.toString() }
                    add { baseBiomeRegName.orEmpty() }
                    add { fillerBlock.toString() }
                    add { topBlock.toString() }
                    client { add { waterColor.toHexColorString() } }
                    add { waterColorMultiplier.toString() }
                    add { spawningChance.toString() }
                    EnumCreatureType.values().forEach { type ->
                        add { getSpawnableList(type).joinToString(separator = System.lineSeparator()) }
                    }
                }
            }
        }
    }
}