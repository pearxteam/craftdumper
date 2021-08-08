@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.entity.ai.brain.schedule.Activity
import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperVillagerProfessions = dumperTable {
    registryName = craftdumper("villager_professions")
    header = listOf("ID", "Name", "Point of Interest", "Specific Items", "Related World Blocks")
    amounts { +ForgeRegistries.PROFESSIONS.keys }
    count { ForgeRegistries.PROFESSIONS.count() }
    table {
        data {
            ForgeRegistries.PROFESSIONS.forEach { profession ->
                row {
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
}

val DumperVillagerPointsOfInterest = dumperTable {
    registryName = craftdumper("villager_points_of_interest")
    header = listOf("ID", "Name", "BlockStates", "Max Tickets", "Valid Range")
    amounts { +ForgeRegistries.POI_TYPES.keys }
    count { ForgeRegistries.POI_TYPES.count() }
    table {
        data {
            ForgeRegistries.POI_TYPES.forEach { poi ->
                row {
                    with(poi) {
                        add { registryName.toString() }
                        add { toString() }
                        add {
                            val result = mutableListOf<String>()
                            val states = blockStates.toMutableSet()

                            while (states.isNotEmpty()) {
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
                        add { validRange.toString() }
                    }
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
        data {
            ForgeRegistries.MEMORY_MODULE_TYPES.forEach { module ->
                row {
                    with(module) {
                        add { registryName.toString() }
                    }
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
        data {
            ForgeRegistries.SENSOR_TYPES.forEach { module ->
                row {
                    with(module) {
                        add { registryName.toString() }
                        add { sensor::class.java.name }
                    }
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
        data {
            ForgeRegistries.SCHEDULES.forEach { module ->
                row {
                    with(module) {
                        add { registryName.toString() }
                        add {
                            val sched = sortedMapOf<Int, Activity>()
                            for ((activity, schedule) in activityToDutiesMap) {
                                for (time in schedule.dutyTimes) {
                                    if (time.active > 0)
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
}

val DumperVillagerActivities = dumperTable {
    registryName = craftdumper("villager_activities")
    header = listOf("ID")
    amounts { +ForgeRegistries.ACTIVITIES.keys }
    count { ForgeRegistries.ACTIVITIES.count() }
    table {
        data {
            ForgeRegistries.ACTIVITIES.forEach { module ->
                row {
                    with(module) {
                        add { registryName.toString() }
                    }
                }
            }
        }
    }
}