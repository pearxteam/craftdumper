@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.tags.ITag
import net.minecraftforge.fml.server.ServerLifecycleHooks
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.buildMultilineString
import net.pearx.craftdumper.common.helper.client
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperDimensionTypes = dumperTable {
    registryName = craftdumper("dimension_types")
    header = listOfNotNull("ID", "Suffix", "Has Skylight", "Has Ceiling", "Is Ultrawarm", "Natural", "Coordinate Scale", "Piglin Safe", "Bed Work", "Respawn Anchor Works", "Has Raids", "Logical Height", "Has Dragon Fight", "Magnifier", "Fixed Time Exist", "Infinite Burn Blocks", client("Effects"))
    amounts { dimensions.forEach { +it.key.registryName } }
    count { dimensions.count() }
    table {
        data {
            dimensions.forEach { (key, dim) ->
                row {
                    add { key.location.toString() }
                    with(dim) {
                        add { suffix }
                        add { hasSkyLight().toPlusMinusString() }
                        add { hasCeiling.toPlusMinusString() }
                        add { isUltrawarm.toPlusMinusString() }
                        add { isNatural.toPlusMinusString() }
                        add { coordinateScale.toString() }
                        add { isPiglinSafe.toPlusMinusString() }
                        add { doesBedWork().toPlusMinusString() }
                        add { doesRespawnAnchorWorks().toPlusMinusString() }
                        add { isHasRaids.toPlusMinusString() }
                        add { logicalHeight.toString() }
                        add { doesHasDragonFight().toString() }
                        add { magnifier::class.java.name }
                        add { doesFixedTimeExist().toPlusMinusString() }
                        add {
                            val infin = isInfiniBurn
                            if (infin is ITag.INamedTag) {
                                buildString {
                                    append('[')
                                    infin.name.toString()
                                    append(']')
                                }
                            } else {
                                buildMultilineString(infin.allElements) {
                                    append(it.registryName.toString())
                                }
                            }
                        }
                        client { add { effects.toString() } }
                    }
                }
            }
        }
    }
}

private val dimensions
    get() = ServerLifecycleHooks.getCurrentServer().dynamicRegistries.func_230520_a_().entries // dims