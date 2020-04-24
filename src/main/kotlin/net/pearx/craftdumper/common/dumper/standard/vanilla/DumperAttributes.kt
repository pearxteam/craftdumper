@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.*
import net.pearx.craftdumper.common.helper.internal.craftdumper

val DumperAttributes = dumperTable {
    registryName = craftdumper("attributes")
    header = listOfNotNull("Name", "Translation Key", client("Display Name"), "Default Value", "Parent", "Class Name", "Should Watch")
    count { getAttributes(true).count() }
    table {
        data {
            getAttributes(true).forEach { attribute ->
                row {
                    add { attribute.name }
                    add { attribute.translationKey }
                    client { add { attribute.displayName } }
                    add { attribute.defaultValue.toString() }
                    add { attribute.parent?.name.orEmpty() }
                    add { attribute::class.java.name }
                    add { attribute.shouldWatch.toPlusMinusString() }
                }
            }
        }
    }
}