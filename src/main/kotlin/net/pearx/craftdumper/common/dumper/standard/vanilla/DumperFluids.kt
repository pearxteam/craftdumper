@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toHexColorString
import net.pearx.craftdumper.common.helper.toPlusMinusString
import net.pearx.craftdumper.common.helper.toTexturesPath

val DumperFluids = dumperTable {
    registryName = craftdumper("fluids")
    header = listOf("ID", "Class Name", "Display Name", "Translation Key", "Still Texture", "Flowing Texture", "Overlay Texture", "Fill Sound", "Empty Sound", "Luminosity", "Density", "Temperature", "Viscosity", "Is Gaseous", "Rarity", "Color", "Is Lighter than Air", "Tags", "FluidState Properties")
    amounts { +ForgeRegistries.FLUIDS.keys }
    count { ForgeRegistries.FLUIDS.count() }
    table {
        ForgeRegistries.FLUIDS.values.forEach { fluid ->
            row(header.size) {
                with(fluid) {
                    val stack = FluidStack(this, 1)
                    add { registryName.toString() }
                    add { this::class.java.name }
                    with(attributes) {
                        add { getDisplayName(stack).unformattedComponentText }
                        add { getTranslationKey(stack) }
                        add { getStill(stack)?.toTexturesPath().orEmpty() }
                        add { getFlowing(stack)?.toTexturesPath().orEmpty() }
                        add { overlayTexture?.toTexturesPath().orEmpty() }
                        add { getFillSound(stack)?.registryName?.toString().orEmpty() }
                        add { getEmptySound(stack)?.registryName?.toString().orEmpty() }
                        add { getLuminosity(stack).toString() }
                        add { getDensity(stack).toString() }
                        add { getTemperature(stack).toString() }
                        add { getViscosity(stack).toString() }
                        add { isGaseous(stack).toPlusMinusString() }
                        add { getRarity(stack).toString() }
                        add { getColor(stack).toHexColorString() }
                        add { isLighterThanAir.toPlusMinusString() }
                    }
                    add { tags.joinToString(System.lineSeparator()) { it.toString() } }
                    add { stateContainer.properties.joinToString(separator = System.lineSeparator()) }
                }
            }
        }
    }
}