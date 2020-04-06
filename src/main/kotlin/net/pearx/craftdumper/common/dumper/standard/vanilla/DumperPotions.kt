//@file:JvmMultifileClass
//@file:JvmName("VanillaDumpers")
//
//package net.pearx.craftdumper.common.dumper.standard.vanilla
//
//import net.minecraft.util.text.translation.I18n
//import net.minecraftforge.fml.common.registry.ForgeRegistries
//import net.pearx.craftdumper.common.dumper.add
//import net.pearx.craftdumper.common.dumper.dumperTable
//import net.pearx.craftdumper.common.dumper.row
//import net.pearx.craftdumper.common.helper.*
//import net.pearx.craftdumper.common.helper.internal.craftdumper
//
//val DumperPotions = dumperTable {
//    registryName = craftdumper("potions")
//    header = listOfNotNull("ID", "Display Name", "Name", "Class Name", "Is Bad Effect", "Is Instant", client("Is Beneficial"), client("Status Icon Index"), client("Liquid Color"), "Curative Items", client("Attribute Modifiers"))
//    amounts { +ForgeRegistries.POTIONS.keys }
//    count { ForgeRegistries.POTIONS.count() }
//    table {
//        ForgeRegistries.POTIONS.forEach { potion ->
//            row(header.size) {
//                with(potion) {
//                    add { registryName.toString() }
//                    add { @Suppress("DEPRECATION") I18n.translateToLocalFormatted(name) }
//                    add { name }
//                    add { this::class.java.name }
//                    add { isBadEffect.toPlusMinusString() }
//                    add { isInstant.toPlusMinusString() }
//                    client {
//                        add { isBeneficial.toPlusMinusString() }
//                        add { statusIconIndex.toString() }
//                        add { liquidColor.toHexColorString() }
//                    }
//                    add {
//                        buildMultilineString(curativeItems) {
//                            it.appendTo(this)
//                        }
//                    }
//                    client {
//                        add {
//                            buildMultilineString(attributeModifierMap.entries) {
//                                val (attribute, modifier) = it
//                                append(attribute.name)
//                                append(": ")
//                                append(modifier)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}