package net.pearx.craftdumper.common.helper

import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.ICraftingRecipe
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.IRecipeType
import net.minecraft.util.NonNullList
import net.minecraftforge.common.crafting.IShapedRecipe
import net.minecraftforge.registries.ForgeRegistries

inline fun <C : IInventory, T : IRecipe<C>> eachRecipe(type: IRecipeType<T>, filter: (T) -> Boolean = { true }, block: (recipe: T) -> Unit) {
    for (recipe in defaultWorld.recipeManager.getRecipes(type).values) {
        if (filter(recipe as T)) {
            block(recipe)
        }
    }
}

inline fun <C : IInventory, T : IRecipe<C>> countRecipes(type: IRecipeType<T>, filter: (T) -> Boolean = { true }): Int {
    return defaultWorld.recipeManager.getRecipes(type).values.count { filter(it as T) }
}



//private inline fun eachShapeless(block: (recipe: IRecipe) -> Unit) {
//    for (recipe in ForgeRegistries.RECIPES)
//        if (recipe !is IShapedRecipe)
//            block(recipe)
//}
//
//val recipes
//    get() = defaultWorld.recipeManager.recipes