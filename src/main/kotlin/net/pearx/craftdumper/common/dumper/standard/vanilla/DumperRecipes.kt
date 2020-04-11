@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.inventory.IInventory
import net.minecraft.item.crafting.AbstractCookingRecipe
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.IRecipeType
import net.minecraftforge.common.crafting.IShapedRecipe
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.*
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperSmeltingRecipes = dumperRecipesCooking("smelting", IRecipeType.SMELTING)

val DumperBlastingRecipes = dumperRecipesCooking("blasting", IRecipeType.BLASTING)

val DumperSmokingRecipes = dumperRecipesCooking("smoking", IRecipeType.SMOKING)

val DumperCampfireCookingRecipes = dumperRecipesCooking("campfire_cooking", IRecipeType.CAMPFIRE_COOKING)

val DumperStonecuttingRecipes = dumperRecipesSimpleIngredients("stonecutting", IRecipeType.STONECUTTING, { true })

val DumperShapelessRecipes = dumperRecipesSimpleIngredients("shapeless", IRecipeType.CRAFTING, { it !is IShapedRecipe<*> }, "Experience", "Cook Time")

val DumperShapedRecipes = dumperRecipes("shaped", IRecipeType.CRAFTING, { it is IShapedRecipe<*> }, "Input Pattern", "Input Ingredients", "Output Item", "Width", "Height") { recipe ->
    with(recipe as IShapedRecipe<*>) {
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

private inline fun <C : IInventory, T : IRecipe<C>> dumperRecipes(name: String, type: IRecipeType<T>, crossinline filter: (T) -> Boolean, vararg elements: String, crossinline customDump: MutableList<String>.(T) -> Unit) = dumperTable {
    registryName = craftdumper("${name}_recipes")
    header = listOf("ID", "Input Ingredients", "Output Item", *elements, "Group", "Serializer", "Icon", "Is Dynamic", "Class")
    amounts {
        eachRecipe(type, filter) {
            +it.id
        }
    }
    count { countRecipes(type, filter) }
    table {
        eachRecipe(type, filter) { recipe ->
            row(header.size) {
                with(recipe) {
                    add { id.toString() }
                    customDump(recipe)
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

private inline fun <C : IInventory, T : IRecipe<C>> dumperRecipesSimpleIngredients(name: String, type: IRecipeType<T>, crossinline filter: (T) -> Boolean, vararg elements: String, crossinline customDump: MutableList<String>.(T) -> Unit = {}) = dumperRecipes(name, type, filter, *elements) { recipe ->
    with(recipe) {
        add {
            buildMultilineString(ingredients) {
                it.appendTo(this)
            }
        }
        add { recipeOutput.toFullString() }
    }
    customDump(recipe)
}

private fun <T : AbstractCookingRecipe> dumperRecipesCooking(name: String, type: IRecipeType<T>) = dumperRecipesSimpleIngredients(name, type, { true }, "Experience", "Cook Time") { recipe ->
    with(recipe) {
        add { experience.toString() }
        add { cookTime.toString() }
    }
}