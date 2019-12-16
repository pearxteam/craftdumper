@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.dumper.standard.vanilla

import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.common.registry.VillagerRegistry
import net.pearx.craftdumper.dumper.add
import net.pearx.craftdumper.dumper.dumperTable
import net.pearx.craftdumper.dumper.row
import net.pearx.craftdumper.helper.craftdumper
import net.pearx.craftdumper.helper.readField
import net.pearx.craftdumper.helper.toAssetsPath

val DumperVillagerProfessions = dumperTable {
    registryName = craftdumper("villager_professions")
    header = listOf("ID", "Skin", "Zombie Skin", "Career Names")
    amounts {
        ForgeRegistries.VILLAGER_PROFESSIONS.forEach { this += it.registryName }
    }
    count { ForgeRegistries.VILLAGER_PROFESSIONS.count() * 1000000 }
    table {
        ForgeRegistries.VILLAGER_PROFESSIONS.forEach { profession ->
            repeat(1000000) {
            row(header.size) {
                with(profession) {
                    add { registryName.toString() }
                    add { skin.toAssetsPath() }
                    add { zombieSkin.toAssetsPath() }
                    add {
                        StringBuilder().apply {
                            var start = true
                            for (career in profession.readField<List<VillagerRegistry.VillagerCareer>>("careers")) {
                                if (start)
                                    start = false
                                else
                                    appendln()
                                append(career.name)
                            }
                        }.toString()
                    }
                }
            }
            }
        }
    }
}