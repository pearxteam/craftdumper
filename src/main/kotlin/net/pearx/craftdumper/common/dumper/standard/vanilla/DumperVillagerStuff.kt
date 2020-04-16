@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.entity.ai.brain.schedule.Activity
import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperVillagerProfessions = dumperTable {
    registryName = craftdumper("villager_professions")
    header = listOf("ID", "Name", "Point of Interest", "Specific Items", "Related World Blocks")
    amounts { +ForgeRegistries.PROFESSIONS.keys }
    count { ForgeRegistries.PROFESSIONS.count() }
    table {
        ForgeRegistries.PROFESSIONS.forEach { profession ->
            row(header.size) {
                with(profession) {
                    add { registryName.toString() }
                    add { toString() }
                    add { pointOfInterest.registryName.toString() }
                    add { specificItems.joinToString(System.lineSeparator()) { it.registryName.toString() } }
                    add { relatedWorldBlocks.joinToString(System.lineSeparator()) { it.registryName.toString() } }
                }
            }
        }
    }
}

val DumperVillagerPointsOfInterest = dumperTable {
    registryName = craftdumper("villager_points_of_interest")
    header = listOf("ID", "Name", "BlockStates", "Max Tickets", /*"Work Sound", */"Valid Range")
    amounts { +ForgeRegistries.POI_TYPES.keys }
    count { ForgeRegistries.POI_TYPES.count() }
    table {
        ForgeRegistries.POI_TYPES.forEach { poi ->
            row(header.size) {
                with(poi) {
                    add { registryName.toString() }
                    add { toString() }
                    add {
                        val result = mutableListOf<String>()
                        val states = blockStates.toMutableSet()

                        while(states.isNotEmpty()) {
                            val first = states.first()
                            val valid = first.block.stateContainer.validStates
                            if (states.containsAll(valid)) {
                                states.removeAll(valid)
                                result += valid.first().block.registryName.toString()
                            }
                            else {
                                states.remove(first)
                                result += first.toString()
                            }
                        }
                        result.joinToString(System.lineSeparator())
                    }
                    add { maxFreeTickets.toString() }
//                    add { workSound?.registryName?.toString().orEmpty() }
                    add { func_225478_d().toString() }
                }
            }
        }
    }
}

val DumperVillagerMemoryModules = dumperTable {
    registryName = craftdumper("villager_memory_modules")
    header = listOf("ID")
    amounts { +ForgeRegistries.MEMORY_MODULE_TYPES.keys }
    count { ForgeRegistries.MEMORY_MODULE_TYPES.count() }
    table {
        ForgeRegistries.MEMORY_MODULE_TYPES.forEach { module ->
            row(header.size) {
                with(module) {
                    add { registryName.toString() }
                }
            }
        }
    }
}

val DumperVillagerSensors = dumperTable {
    registryName = craftdumper("villager_sensors")
    header = listOf("ID", "Sensor Class")
    amounts { +ForgeRegistries.SENSOR_TYPES.keys }
    count { ForgeRegistries.SENSOR_TYPES.count() }
    table {
        ForgeRegistries.SENSOR_TYPES.forEach { module ->
            row(header.size) {
                with(module) {
                    add { registryName.toString() }
                    add { func_220995_a()::class.java.name }
                }
            }
        }
    }
}

val DumperVillagerSchedules = dumperTable {
    registryName = craftdumper("villager_schedules")
    header = listOf("ID", "Start Time - Activity")
    amounts { +ForgeRegistries.SCHEDULES.keys }
    count { ForgeRegistries.SCHEDULES.count() }
    table {
        ForgeRegistries.SCHEDULES.forEach { module ->
            row(header.size) {
                with(module) {
                    add { registryName.toString() }
                    add {
                        val sched = sortedMapOf<Int, Activity>()
                        for((activity, schedule) in field_221387_e) {
                            for(time in schedule.field_221396_a) {
                                if(time.func_221389_b() > 0)
                                    sched[time.duration] = activity
                            }
                        }
                        sched.entries.joinToString(System.lineSeparator()) { (key, value) -> "$key - ${value.registryName}" }
                    }
                }
            }
        }
    }
}

val DumperVillagerActivities = dumperTable {
    registryName = craftdumper("villager_activities")
    header = listOf("ID")
    amounts { +ForgeRegistries.ACTIVITIES.keys }
    count { ForgeRegistries.ACTIVITIES.count() }
    table {
        ForgeRegistries.ACTIVITIES.forEach { module ->
            row(header.size) {
                with(module) {
                    add { registryName.toString() }
                }
            }
        }
    }
}