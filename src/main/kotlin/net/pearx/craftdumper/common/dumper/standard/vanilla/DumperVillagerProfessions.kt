//@file:JvmMultifileClass
//@file:JvmName("VanillaDumpers")
//
//package net.pearx.craftdumper.common.dumper.standard.vanilla
//
//import net.minecraftforge.fml.common.registry.ForgeRegistries
//import net.minecraftforge.fml.common.registry.VillagerRegistry
//import net.minecraftforge.registries.ForgeRegistries
//import net.pearx.craftdumper.common.dumper.add
//import net.pearx.craftdumper.common.dumper.dumperTable
//import net.pearx.craftdumper.common.dumper.row
//import net.pearx.craftdumper.common.helper.internal.craftdumper
//import net.pearx.craftdumper.common.helper.readField
//import net.pearx.craftdumper.common.helper.toAssetsPath
//
//val DumperVillagerProfessions = dumperTable {
//    registryName = craftdumper("villager_professions")
//    header = listOf("ID", "Skin", "Zombie Skin", "Career Names")
//    amounts { +ForgeRegistries.PROFESSIONS.keys }
//    count { ForgeRegistries.PROFESSIONS.count() }
//    table {
//        ForgeRegistries.PROFESSIONS.forEach { profession ->
//            row(header.size) {
//                with(profession) {
//                    add { registryName.toString() }
//                    add { skin.toAssetsPath() }
//                    add { zombieSkin.toAssetsPath() }
//                    add {
//                        profession.readField<List<VillagerRegistry.VillagerCareer>>("careers").joinToString(System.lineSeparator()) { it.name }
//                    }
//                }
//            }
//        }
//    }
//}
// todo