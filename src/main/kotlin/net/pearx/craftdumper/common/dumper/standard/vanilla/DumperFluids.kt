@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toHexColorString
import net.pearx.craftdumper.common.helper.toPlusMinusString
import net.pearx.craftdumper.common.helper.toTexturesPath

val DumperFluids = dumperTable {
    registryName = craftdumper("fluids")
    header = listOf("ID", "Unlocalized Name", "Display Name", "Still Texture", "Flowing Texture", "Overlay Texture", "Fill Sound", "Empty Sound", "Luminosity", "Density", "Temperature", "Viscosity", "Is Gaseous", "Rarity", "Block", "Color", "Is Lighter than Air", "Can be Placed in World")
    amounts {
        for (fluid in FluidRegistry.getRegisteredFluids().values)
            this += FluidRegistry.getModId(FluidStack(fluid, 1)) ?: ""
    }
    count { FluidRegistry.getRegisteredFluids().count() }
    table {
        FluidRegistry.getRegisteredFluids().values.forEach { fluid ->
            row(header.size) {
                with(fluid) {
                    val stack = FluidStack(fluid, 1)
                    val modId = FluidRegistry.getModId(stack).orEmpty()
                    add { ResourceLocation(modId, name).toString() }
                    add { getUnlocalizedName(stack) }
                    add { getLocalizedName(stack) }
                    add { getStill(stack).toTexturesPath() }
                    add { getFlowing(stack).toTexturesPath() }
                    add { overlay?.toTexturesPath().orEmpty() }
                    add { getFillSound(stack).registryName.toString() }
                    add { getEmptySound(stack).registryName.toString() }
                    add { getLuminosity(stack).toString() }
                    add { getDensity(stack).toString() }
                    add { getTemperature(stack).toString() }
                    add { getViscosity(stack).toString() }
                    add { isGaseous(stack).toPlusMinusString() }
                    add { getRarity(stack).toString() }
                    add { block?.registryName?.toString().orEmpty() }
                    add { getColor(stack).toHexColorString() }
                    add { isLighterThanAir.toPlusMinusString() }
                    add { canBePlacedInWorld().toPlusMinusString() }
                }
            }
        }
    }
}