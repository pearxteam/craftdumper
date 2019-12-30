@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.minecraft.advancements.Advancement
import net.minecraftforge.common.DimensionManager
import net.pearx.craftdumper.common.dumper.add
import net.pearx.craftdumper.common.dumper.dumperTable
import net.pearx.craftdumper.common.dumper.row
import net.pearx.craftdumper.common.helper.*
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperAdvancements = dumperTable {
    registryName = craftdumper("advancements")
    header = listOfNotNull("ID", "Display Text", "Title", "Description", client("Icon"), client("X"), client("Y"), client("Background"), "Frame", "Is Hidden", "Should Announce", client("Should Show Toast"), "Criteria", "Requirements", "Parent", "Children", "Reward Experience", "Reward Loot", "Reward Recipes", "Reward Function")
    amounts { collectAdvancements().forEach { this += it.id } }
    count { collectAdvancements().size }
    table {
        collectAdvancements().forEach { adv ->
            row(header.size) {
                with(adv) {
                    add { id.toString() }
                    add { displayText.formattedText }
                    display?.run {
                        add { title.formattedText }
                        add { description.formattedText }
                        client {
                            add { icon.toFullString() }
                            add { x.toString() }
                            add { y.toString() }
                            add { background?.toAssetsPath().orEmpty() }
                        }
                        add { frame.toString() }
                        add { isHidden.toPlusMinusString() }
                        add { shouldAnnounceToChat().toPlusMinusString() }
                        client { add { shouldShowToast().toPlusMinusString() } }
                    } ?: repeat(if (isClient) 10 else 5) { add("") }
                    add {
                        buildMultilineString(criteria.entries) { (key, value) ->
                            append(key)
                            append(": ")
                            append(value.criterionInstance?.id?.toString().orEmpty())
                        }
                    }
                    add {
                        buildMultilineString(requirements) { req ->
                            req.joinTo(this, separator = " | ")
                        }
                    }
                    add { parent?.id?.toString().orEmpty() }
                    add {
                        buildMultilineString(children) {
                            append(it.id.toString())
                        }
                    }
                    rewards.apply {
                        add { experience.toString() }
                        add { loot.joinToString(separator = System.lineSeparator()) }
                        add { recipes.joinToString(separator = System.lineSeparator()) }
                        add { function?.toString().orEmpty() }
                    }
                }
            }
        }
    }
}

private fun collectAdvancements(): List<Advancement> {
    val advancements = mutableListOf<Advancement>()
    for (w in DimensionManager.getWorlds())
        for (adv in w.advancementManager.advancements)
            if (adv !in advancements)
                advancements += adv
    return advancements
}