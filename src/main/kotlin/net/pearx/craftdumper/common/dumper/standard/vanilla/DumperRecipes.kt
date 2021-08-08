@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.inventory.IInventory
import net.minecraft.item.crafting.AbstractCookingRecipe
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.IRecipeType
import net.minecraftforge.common.crafting.IShapedRecipe
import net.pearx.craftdumper.common.dumper.dsl.DumperTableDataContext
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.*
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperSmeltingRecipes = dumperRecipesCooking("smelting", IRecipeType.SMELTING)

val DumperBlastingRecipes = dumperRecipesCooking("blasting", IRecipeType.BLASTING)

val DumperSmokingRecipes = dumperRecipesCooking("smoking", IRecipeType.SMOKING)

val DumperCampfireCookingRecipes = dumperRecipesCooking("campfire_cooking", IRecipeType.CAMPFIRE_COOKING)

val DumperStonecuttingRecipes = dumperRecipesSimpleIngredients("stonecutting", IRecipeType.STONECUTTING, { true })

val DumperShapelessRecipes = dumperRecipesSimpleIngredients("shapeless", IRecipeType.CRAFTING, { it !is IShapedRecipe<*> })

val DumperShapedRecipes = dumperRecipes("shaped", IRecipeType.CRAFTING, { it is IShapedRecipe<*> }, "Input Pattern", "Input Ingredients", "Output Item", "Width", "Height") { recipe, list ->
    with(recipe as IShapedRecipe<*>) {
        with(list) {
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
        }
    }
}

private typealias CustomDump<T> = DumperTableDataContext.(recipe: T, list: MutableList<String>) -> Unit

private inline fun <C : IInventory, T : IRecipe<C>> dumperRecipes(name: String, type: IRecipeType<T>, crossinline filter: (T) -> Boolean, vararg elements: String, crossinline customDump: CustomDump<T>) = dumperTable {
    registryName = craftdumper("${name}_recipes")
    header = listOf("ID", *elements, "Group", "Serializer", "Icon", "Is Dynamic", "Class")
    amounts {
        eachRecipe(type, filter) {
            +it.id
        }
    }
    count { countRecipes(type, filter) }
    table {
        data {
            eachRecipe(type, filter) { recipe ->
                row {
                    with(recipe) {
                        add { id.toString() }
                        customDump(recipe, this@row)
                        add { group }
                        add { serializer.registryName.toString() }
                        add { icon.toFullString() }
                        add { isDynamic.toPlusMinusString() }
                        add { this::class.java.name }
                    }
                }
            }
        }
    }
}

private inline fun <C : IInventory, T : IRecipe<C>> dumperRecipesSimpleIngredients(name: String, type: IRecipeType<T>, crossinline filter: (T) -> Boolean, vararg elements: String, crossinline customDump: CustomDump<T> = { _, _ -> }) = dumperRecipes(name, type, filter, "Input Ingredients", "Output Item", *elements) { recipe, list ->
    with(recipe) {
        with(list) {
            add {
                buildMultilineString(ingredients) {
                    it.appendTo(this)
                }
            }
            add { recipeOutput.toFullString() }
        }
    }
    customDump(recipe, list)
}

private fun <T : AbstractCookingRecipe> dumperRecipesCooking(name: String, type: IRecipeType<T>) = dumperRecipesSimpleIngredients(name, type, { true }, "Experience", "Cook Time") { recipe, list ->
    with(recipe) {
        with(list) {
            add { experience.toString() }
            add { cookTime.toString() }
        }
    }
}