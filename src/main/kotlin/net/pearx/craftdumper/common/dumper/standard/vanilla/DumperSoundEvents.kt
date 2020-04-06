@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.client.Minecraft
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
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
        ForgeRegistries.SOUND_EVENTS.forEach { event ->
            row(header.size) {
                add { event.registryName.toString() }
                client {
                    val accessor = Minecraft.getInstance().soundHandler.getAccessor(event.name)!!
                    val subtitle = accessor.subtitle
                    add {
                        subtitle?.unformattedComponentText.orEmpty()
                    }
                    add {
                        if (subtitle is TranslationTextComponent) {
                            subtitle.key
                        }
                        else
                            subtitle.toString()
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