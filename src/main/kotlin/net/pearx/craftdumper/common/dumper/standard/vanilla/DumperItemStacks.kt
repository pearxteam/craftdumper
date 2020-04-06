//@file:JvmMultifileClass
//@file:JvmName("VanillaDumpers")
//
//package net.pearx.craftdumper.common.dumper.standard.vanilla
//
//import moze_intel.projecte.utils.EMCHelper
//import net.minecraft.client.Minecraft
//import net.minecraft.client.util.ITooltipFlag
//import net.minecraft.item.Item
//import net.minecraft.item.ItemBlock
//import net.minecraft.tileentity.TileEntityFurnace
//import net.minecraftforge.client.ItemModelMesherForge
//import net.minecraftforge.fml.common.Loader
//import net.minecraftforge.oredict.OreDictionary
//import net.pearx.craftdumper.PROJECTE_ID
//import net.pearx.craftdumper.common.dumper.add
//import net.pearx.craftdumper.common.dumper.dumperTable
//import net.pearx.craftdumper.common.dumper.row
//import net.pearx.craftdumper.common.helper.*
//import net.pearx.craftdumper.common.helper.internal.craftdumper
//
//val DumperItemStacks = dumperTable {
//    registryName = craftdumper("item_stacks")
//    header = listOfNotNull("ID", "Metadata", "NBT Tag Compound", "Display Name", client("Tooltip"), "Translation Key", "Class Name", "Is ItemBlock", "OreDict Names", "Max Stack Size", "Max Damage", "Burn Time", client("Model Name"), ifOrNull(Loader.isModLoaded(PROJECTE_ID), "EMC"))
//    amounts {
//        eachStack<Item> { item, _ ->
//            +item.registryName
//        }
//    }
//    count { stackCount<Item>() }
//    table {
//        eachStack<Item> { item, stack ->
//            row(header.size) {
//                with(item) {
//                    add { registryName.toString() }
//                    add { stack.metadata.toString() }
//                    add { stack.tagCompound?.toString().orEmpty() }
//                    add { getItemStackDisplayName(stack) }
//                    client { add { mutableListOf<String>().apply { addInformation(stack, Minecraft.getMinecraft().world, this, ITooltipFlag.TooltipFlags.NORMAL) }.joinToString(separator = System.lineSeparator()) } }
//                    add { getTranslationKey(stack) }
//                    add { this::class.java.name }
//                    add { (this is ItemBlock).toPlusMinusString() }
//                    add {
//                        buildMultilineString(OreDictionary.getOreIDs(stack)) { id ->
//                            append(OreDictionary.getOreName(id))
//                        }
//                    }
//                    add { getItemStackLimit(stack).toString() }
//                    add { getMaxDamage(stack).toString() }
//                    add { TileEntityFurnace.getItemBurnTime(stack).toString() }
//                    client { add { (Minecraft.getMinecraft().renderItem.itemModelMesher as ItemModelMesherForge).getLocation(stack).toString() } }
//                    if (Loader.isModLoaded(PROJECTE_ID))
//                        add { if (EMCHelper.doesItemHaveEmc(stack)) EMCHelper.getEmcValue(stack).toString() else "" }
//                }
//            }
//        }
//    }
//}