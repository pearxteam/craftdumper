//@file:JvmMultifileClass
//@file:JvmName("VanillaDumpers")
//
//package net.pearx.craftdumper.common.dumper.standard.vanilla
//
//import net.minecraftforge.oredict.OreDictionary
//import net.pearx.craftdumper.common.dumper.add
//import net.pearx.craftdumper.common.dumper.dumperTable
//import net.pearx.craftdumper.common.dumper.row
//import net.pearx.craftdumper.common.helper.internal.craftdumper
//import net.pearx.craftdumper.common.helper.toFullString
//
//val DumperOreDictionary = dumperTable {
//    registryName = craftdumper("ore_dictionary")
//    header = listOf("Ore Name", "Items")
//    amounts { OreDictionary.getOreNames().forEach { it += OreDictionary.getOres(it).size } }
//    count { OreDictionary.getOreNames().count() }
//    table {
//        OreDictionary.getOreNames().forEach { ore ->
//            row(header.size) {
//                add { ore }
//                add { OreDictionary.getOres(ore).joinToString(System.lineSeparator()) { it.toFullString(true) } }
//            }
//        }
//    }
//}