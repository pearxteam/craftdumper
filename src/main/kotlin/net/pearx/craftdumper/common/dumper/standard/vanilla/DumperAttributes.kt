@file:JvmMultifileClass
@file:JvmName("VanillaDumpers")

package net.pearx.craftdumper.common.dumper.standard.vanilla

import net.pearx.craftdumper.common.dumper.dsl.dumperTable
import net.pearx.craftdumper.common.helper.client
import net.pearx.craftdumper.common.helper.displayName
import net.pearx.craftdumper.common.helper.getAttributes
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.toPlusMinusString

val DumperAttributes = dumperTable {
    registryName = craftdumper("attributes")
    header = listOfNotNull("Name", "Translation Key", client("Display Name"), "Default Value", "Class Name", "Should Watch")
    count { getAttributes(true).count() }
    table {
        data {
            getAttributes(true).forEach { attribute ->
                row {
                    add { attribute.registryName.toString() }
                    add { attribute.attributeName }
                    client { add { attribute.displayName } }
                    add { attribute.defaultValue.toString() }
                    add { attribute::class.java.name }
                    add { attribute.shouldWatch.toPlusMinusString() }
                }
            }
        }
    }
}