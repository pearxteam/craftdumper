@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import moze_intel.projecte.utils.EMCHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.BlockItem
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.client.ItemModelMesherForge
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.fml.ModList
import net.pearx.craftdumper.PROJECTE_ID
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.*
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperItemStacks = dumperTable {
    registryName = craftdumper("item_stacks")
    header = listOfNotNull("ID", "Tag", "Display Name", client("Tooltip"), "Translation Key", "Class Name", "Is BlockItem", "Tags", "Max Stack Size", "Max Damage", "Burn Time", "Enchantability", client("Model Name"), ifOrNull(ModList.get().isLoaded(PROJECTE_ID), "EMC"))
    amounts {
        eachStack { item, _ ->
            +item.registryName
        }
    }
    count { countStacks() }
    table {
        data {
            eachStack { item, stack ->
                row {
                    with(item) {
                        add { registryName.toString() }
                        add { stack.tag?.toString().orEmpty() }
                        add { getDisplayName(stack).unformattedComponentText }
                        client { add { mutableListOf<ITextComponent>().apply { addInformation(stack, Minecraft.getInstance().world, this, ITooltipFlag.TooltipFlags.NORMAL) }.joinToString(separator = System.lineSeparator()) { it.unformattedComponentText } } }
                        add { getTranslationKey(stack) }
                        add { this::class.java.name }
                        add { (this is BlockItem).toPlusMinusString() }
                        add {
                            tags.joinToString(System.lineSeparator())
                        }
                        add { getItemStackLimit(stack).toString() }
                        add { getMaxDamage(stack).toString() }
                        add { ForgeHooks.getBurnTime(stack).toString() }
                        add { getItemEnchantability(stack).toString() }
                        client { add { (Minecraft.getInstance().itemRenderer.itemModelMesher as ItemModelMesherForge).getLocation(stack).toString() } }
                        if (ModList.get().isLoaded(PROJECTE_ID))
                            add { if (EMCHelper.doesItemHaveEmc(stack)) EMCHelper.getEmcValue(stack).toString() else "" }
                    }
                }
            }
        }
    }
}