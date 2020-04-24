package net.pearx.craftdumper.common.dumper

import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.common.helper.internal.appendCsvRow

interface DumperTable : Dumper {
    fun createHeader(): List<String>
    fun dumpTable(header: List<String>): Iterable<List<String>>

    override fun dumpData(reporter: DumpProgressReporter): List<DumpOutput> {
        val dumpFile = CraftDumper.getOutputFile(registryName!!, ".csv")
        dumpFile.parentFile.mkdirs()
        dumpFile.printWriter().use { writer ->
            with(writer) {
                val header = createHeader()
                appendCsvRow(header)
                dumpTable(header).forEachIndexed { index, row ->
                    appendln()
                    appendCsvRow(row)
                    reporter.progress = index + 1
                }
            }
        }
        return listOf(DumpOutput("table", dumpFile))
    }
}