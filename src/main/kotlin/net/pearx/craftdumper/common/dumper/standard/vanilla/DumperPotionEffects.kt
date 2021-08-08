@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.client.resources.I18n
import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.*
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperPotionEffects = dumperTable {
    registryName = craftdumper("potion_effects")
    header = listOfNotNull("ID", client("Display Name"), "Name", "Class Name", "Type", "Is Instant", "Liquid Color", "Curative Items", "Attribute Modifiers")
    amounts { +ForgeRegistries.POTIONS.keys }
    count { ForgeRegistries.POTIONS.count() }
    table {
        data {
            ForgeRegistries.POTIONS.forEach { potion ->
                row {
                    with(potion) {
                        add { registryName.toString() }
                        client { add { I18n.format(name) } }
                        add { name }
                        add { this::class.java.name }
                        add { effectType.name }
                        add { isInstant.toPlusMinusString() }
                        add { liquidColor.toHexColorString() }
                        add {
                            buildMultilineString(curativeItems) {
                                it.appendTo(this)
                            }
                        }
                        add {
                            buildMultilineString(attributeModifierMap.entries) {
                                val (attribute, modifier) = it
                                append(attribute.registryName)
                                append(": ")
                                append(modifier)
                            }
                        }
                    }
                }
            }
        }
    }
}