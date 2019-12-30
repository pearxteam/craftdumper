@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.common.crafting.IShapedRecipe
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.appendTo
import net.pearx.craftdumper.common.helper.buildMultilineString
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toFullString
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperShapelessRecipes = dumperTable {
    registryName = craftdumper("shapeless_recipes")
    header = listOf("ID", "Input Ingredients", "Output Item", "Group", "Is Dynamic")
    amounts {
        eachShapeless {
            this += it.registryName
        }
    }
    count { ForgeRegistries.ITEMS.count { it !is IShapedRecipe } }
    table {
        eachShapeless { recipe ->
            row(header.size) {
                with(recipe) {
                    add { registryName.toString() }
                    add {
                        buildMultilineString(ingredients) {
                            it.appendTo(this)
                        }
                    }
                    add { recipeOutput.toFullString() }
                    add { group }
                    add { isDynamic.toPlusMinusString() }
                }
            }
        }
    }
}

private inline fun eachShapeless(block: (recipe: IRecipe) -> Unit) {
    for (recipe in ForgeRegistries.RECIPES)
        if (recipe !is IShapedRecipe)
            block(recipe)
}