@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.client.Minecraft
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.buildMultilineString
import net.pearx.craftdumper.common.helper.client
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toAssetsPath

val DumperSoundEvents = dumperTable {
    registryName = craftdumper("sound_events")
    header = listOfNotNull("ID", client("Subtitle"), client("Raw Subtitle"), client("Sound Path @ Weight"))
    amounts { +ForgeRegistries.SOUND_EVENTS.keys }
    count { ForgeRegistries.SOUND_EVENTS.count() }
    table {
        data {
            ForgeRegistries.SOUND_EVENTS.forEach { event ->
                row {
                    add { event.registryName.toString() }
                    client {
                        val accessor = Minecraft.getInstance().soundHandler.getAccessor(event.name)!!
                        val subtitle = accessor.subtitle
                        add {
                            subtitle?.unformattedComponentText.orEmpty()
                        }
                        add {
                            when (subtitle) {
                                null -> ""
                                is TranslationTextComponent -> subtitle.key
                                else -> subtitle.toString()
                            }
                        }
                        add {
                            buildMultilineString(accessor.accessorList) {
                                append(it.cloneEntry().soundAsOggLocation.toAssetsPath())
                                append(" @ ")
                                append(it.weight)
                            }
                        }
                    }
                }
            }
        }
    }
}