@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.client.Minecraft
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.client
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toAssetsPath

val DumperSoundEvents = dumperTable {
    registryName = craftdumper("sound_events")
    header = listOfNotNull("ID", client("Subtitle"), client("Raw Subtitle"), client("Sound Path @ Weight"))
    amounts { this += ForgeRegistries.SOUND_EVENTS.keys }
    count { ForgeRegistries.SOUND_EVENTS.count() }
    table {
        ForgeRegistries.SOUND_EVENTS.forEach { event ->
            row(header.size) {
                add { event.registryName.toString() }
                client {
                    val accessor = Minecraft.getMinecraft().soundHandler.getAccessor(event.soundName)!!
                    val subtitle = accessor.subtitle
                    add {
                        subtitle?.unformattedText.toString()
                    }
                    add {
                        if(subtitle is TextComponentTranslation) {
                            subtitle.key
                        }
                        else
                            subtitle.toString()
                    }
                    add {
                        StringBuilder().apply {
                            var start = true
                            for (acc in accessor.accessorList) {
                                if(start)
                                    start = false
                                else
                                    appendln()
                                append(acc.cloneEntry().soundAsOggLocation.toAssetsPath())
                                append(" @ ")
                                append(acc.weight)
                            }
                        }.toString()
                    }
                }
            }
        }
    }
}