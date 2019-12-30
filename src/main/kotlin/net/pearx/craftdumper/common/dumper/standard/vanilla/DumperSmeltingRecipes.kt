@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.item.crafting.FurnaceRecipes
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toFullString

val DumperSmeltingRecipes = dumperTable {
    registryName = craftdumper("smelting_recipes")
    header = listOf("Input", "Output", "XP")
    count { FurnaceRecipes.instance().smeltingList.size }
    table {
        val recipes = FurnaceRecipes.instance()
        recipes.smeltingList.entries.forEach { (input, output) ->
            row(header.size) {
                add { input.toFullString(true) }
                add { output.toFullString() }
                add { recipes.getSmeltingExperience(output).toString() }
            }
        }
    }
}