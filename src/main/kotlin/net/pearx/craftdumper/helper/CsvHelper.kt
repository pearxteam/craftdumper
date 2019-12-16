package net.pearx.craftdumper.helper

internal fun Appendable.appendCsvRow(row: List<String>) {
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
