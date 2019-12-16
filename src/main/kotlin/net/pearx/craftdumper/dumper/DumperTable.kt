package net.pearx.craftdumper.dumper

import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.helper.appendCsvRow
import net.pearx.craftdumper.helper.client
import net.pearx.craftdumper.helper.currentDateTime
import net.pearx.craftdumper.helper.getRegistryElementName

typealias DumperTableContents = Iterable<List<String>>

interface DumperTable : Dumper {
    val header: List<String>
    val columnToSortBy: Int
    fun dumpTable(): DumperTableContents

    override fun dumpContents(reporter: DumpProgressReporter): List<DumpOutput> {
        val count = getCount()
        val dumpFile = CraftDumper.outputDirectory
            .resolve("${DumperRegistry.getRegistryElementName(registryName!!)}_${currentDateTime()}.csv")
        dumpFile.parentFile.mkdirs()
        dumpFile.printWriter().use { writer ->
            with(writer) {
                // todo: sorting
                appendCsvRow(header)
                dumpTable().forEachIndexed { index, row ->
                    appendCsvRow(row)
                    reporter.progress = (index + 1.0) / count
                }
            }
        }
        return listOf(DumpOutput("craftdumper.output.table.name", dumpFile))
    }
}

typealias DumperTableCreator = suspend SequenceScope<List<String>>.() -> Unit

class DumperTableContext : DumperBase(), DumperTable {
    private lateinit var tableCreator: DumperTableCreator
    override lateinit var header: List<String>
    override var columnToSortBy = 0

    override fun dumpTable(): DumperTableContents = Iterable { iterator(tableCreator) }

    fun table(block: DumperTableCreator) {
        tableCreator = block
    }
}

inline fun dumperTable(init: DumperTableContext.() -> Unit): DumperTable = DumperTableContext().apply(init)

inline fun dumperTableClient(init: DumperTableContext.() -> Unit): DumperTable? = client { DumperTableContext().apply(init) }

inline fun MutableList<String>.add(block: () -> String) {
    add(
        try {
            block()
        }
        catch (e: Exception) {
            CraftDumper.log.error("An error occurred while creating a dump! Already dumped data: ${joinToString(prefix = "[", separator = ", ", postfix = "]")}", e)
            "*error*"
        }
    )
}

suspend inline fun SequenceScope<List<String>>.row(size: Int, block: MutableList<String>.() -> Unit) {
    yield(ArrayList<String>(size).apply(block))
}