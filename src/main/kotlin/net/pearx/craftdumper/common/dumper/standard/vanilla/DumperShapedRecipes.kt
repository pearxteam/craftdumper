@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraftforge.common.crafting.IShapedRecipe
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.buildMultilineString
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toFullString
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperShapedRecipes = dumperTable {
    registryName = craftdumper("shaped_recipes")
    header = listOf("ID", "Input Pattern", "Input Ingredients", "Output Item", "Width", "Height", "Group", "Is Dynamic")
    amounts {
        eachShaped {
            this += it.registryName
        }
    }
    count { ForgeRegistries.RECIPES.count { it is IShapedRecipe } }
    table {
        eachShaped { recipe ->
            row(header.size) {
                with(recipe) {
                    add { registryName.toString() }
                    if (ingredients.isNotEmpty()) {

                        val ingredientStrings = mutableListOf<String>().apply { ingredients.forEach { add { it.toFullString() } } }
                        val patternMap = hashMapOf<String, Char>().apply {
                            var lastChar = 'A'
                            for (dist in ingredientStrings.distinct()) {
                                if (dist.isNotEmpty()) {
                                    this[dist] = lastChar
                                    lastChar++
                                }
                            }
                        }
                        add {
                            buildMultilineString(0 until recipeHeight) { row ->
                                for (column in 0 until recipeWidth) {
                                    val str = ingredientStrings[row * recipeWidth + column]
                                    append(if (str.isEmpty()) "-" else patternMap[str])
                                }
                            }
                        }
                        add {
                            buildMultilineString(patternMap.entries.sortedBy { it.value }) { (str, char) ->
                                append(char)
                                append(": ")
                                append(str)
                            }
                        }
                    }
                    else
                        repeat(2) { add("") }
                    add { recipeOutput.toFullString() }
                    add { recipeWidth.toString() }
                    add { recipeHeight.toString() }
                    add { group }
                    add { isDynamic.toPlusMinusString() }
                }
            }
        }
    }
}

private inline fun eachShaped(block: (recipe: IShapedRecipe) -> Unit) {
    for (recipe in ForgeRegistries.RECIPES)
        if (recipe is IShapedRecipe)
            block(recipe)
}