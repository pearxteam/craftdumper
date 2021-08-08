//@file:JvmMultifileClass
//@file:JvmName("VanillaDumpers")
//
//package net.pearx.craftdumper.common.dumper.standard.vanilla
//
//import net.minecraft.world.DimensionType
//import net.pearx.craftdumper.common.dumper.dsl.dumperTable
//import net.pearx.craftdumper.common.helper.internal.craftdumper
//import net.pearx.craftdumper.common.helper.toPlusMinusString
//
//val DumperDimensions = dumperTable {
//    registryName = craftdumper("dimensions")
//    header = listOf("ID", "Numeric ID", "Suffix", "Directory", "Has Sky Light", "Is Vanilla")
//    amounts { DimensionType.getAll().forEach { +it.registryName } }
//    count { DimensionType.getAll().count() }
//    table {
//        data {
//            DimensionType.getAll().forEach { dim ->
//                row {
//                    with(dim) {
//                        add { registryName.toString() }
//                        add { id.toString() }
//                        add { suffix }
//                        add { directory }
//                        add { hasSkyLight().toPlusMinusString() }
//                        add { isVanilla.toPlusMinusString() }
//                    }
//                }
//            }
//        }
//    }
//}