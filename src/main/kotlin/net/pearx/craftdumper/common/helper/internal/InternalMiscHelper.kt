@file:JvmMultifileClass
@file:JvmName("InternalHelper")

package net.pearx.craftdumper.common.helper.internal

import net.minecraft.util.ResourceLocation
import net.pearx.craftdumper.ID
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal fun Appendable.appendCsvRow(row: Iterable<String>) {
    var start = true
    for (element in row) {
        if (start)
            start = false
        else
            append(',')

        val shouldBeQuoted = element.any { it == ',' || it == '"' || it == '\r' || it == '\n' }
        if (shouldBeQuoted)
            append('"')

        append(element.replace("\"", "\"\""))

        if (shouldBeQuoted)
            append('"')
    }
}

internal fun Appendable.appendCsvRow(vararg row: String) = appendCsvRow(row.toList())

internal fun currentDateTime(): String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))

internal fun craftdumper(pathIn: String) = ResourceLocation(ID, pathIn)