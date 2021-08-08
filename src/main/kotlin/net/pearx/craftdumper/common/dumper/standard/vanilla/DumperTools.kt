@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import com.mojang.authlib.GameProfile
import net.minecraft.entity.ai.attributes.Attributes
import net.minecraft.inventory.EquipmentSlotType
import net.minecraft.item.*
import net.minecraftforge.common.ToolType
import net.minecraftforge.common.util.FakePlayer
import net.pearx.craftdumper.common.dumper.Dumper
import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.*
import net.pearx.craftdumper.common.helper.internal.craftdumper
import java.util.*

val DumperPickaxes = dumperTools<TieredItem>("pickaxes", ToolType.PICKAXE)
val DumperAxes = dumperTools<TieredItem>("axes", ToolType.AXE)
val DumperShovels = dumperTools<TieredItem>("shovels", ToolType.SHOVEL)
val DumperHoes = dumperTools<HoeItem>("hoes")
val DumperSwords = dumperTools<SwordItem>("swords")


private inline fun <reified T : TieredItem> dumperTools(name: String, type: ToolType? = null): Dumper {
    return dumperTable {
        registryName = craftdumper(name)
        header = listOfNotNull("Item", "Attack Damage Modifier", "Attack Damage", "Attack Speed Modifier", "Attack Speed", "Durability", "Efficiency", "Harvest Level", "Enchantability", "Repair Material")
        amounts {
            eachTiered<T>(type) { item, _ ->
                +item.registryName
            }
        }
        count { tieredCount<T>(type) }
        table {
            data {
                val fakePlayer = FakePlayer(defaultWorld, GameProfile(UUID(0, 0), "craftdumper"))
                eachTiered<T>(type) { item, stack ->
                    row {
                        with(item) {
                            add { stack.toFullString() }
                            val modifiers = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND)
                            val damage = modifiers[Attributes.ATTACK_DAMAGE].first { it.id == Item.ATTACK_DAMAGE_MODIFIER }.amount
                            val speed = modifiers[Attributes.ATTACK_SPEED].first { it.id == Item.ATTACK_SPEED_MODIFIER }.amount
                            add { "+${damage.round(5)}" }
                            add { "${(fakePlayer.getAttribute(Attributes.ATTACK_DAMAGE)!!.baseValue + damage).round(5)}"}
                            add { "-${-speed.round(5)}" }
                            add { "${(fakePlayer.getAttribute(Attributes.ATTACK_SPEED)!!.baseValue + speed).round(5)}"}
                            add { stack.getDurabilityString() }
                            add { tier.efficiency.toString() }
                            add { tier.harvestLevel.toString() }
                            add { stack.itemEnchantability.toString() }
                            add { tier.repairMaterial.toFullString() }
                        }
                    }
                }
            }
        }
    }
}

private inline fun <reified T : TieredItem> tieredCount(type: ToolType?) = countStacks({ it is T }, { type == null || type in it.toolTypes })
private inline fun <reified T : TieredItem> eachTiered(type: ToolType?, block: (item: TieredItem, stack: ItemStack) -> Unit) = eachStack({ type == null || type in it.toolTypes }, { it is T }) { item, stack -> block(item as T, stack) }