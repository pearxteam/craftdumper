@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.client.Minecraft
import net.minecraft.entity.player.PlayerInventory
import net.minecraftforge.registries.ForgeRegistries
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.client
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperContainers = dumperTable {
    registryName = craftdumper("containers")
    header = listOfNotNull("ID", client("Class"))
    amounts { +ForgeRegistries.CONTAINERS.keys }
    count { ForgeRegistries.CONTAINERS.count() }
    table {
        data {
            ForgeRegistries.CONTAINERS.forEach { container ->
                row {
                    with(container) {
                        add { registryName.toString() }
                        client {
                            add { create(Int.MAX_VALUE, PlayerInventory(Minecraft.getInstance().player!!))::class.java.name }
                        }
                    }
                }
            }
        }
    }
}