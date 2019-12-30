package net.pearx.craftdumper.common.dumper

import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.common.helper.client
import net.pearx.craftdumper.common.helper.internal.appendCsvRow

typealias DumperTableData = Iterable<List<String>>

interface DumperTable : Dumper {
    val header: List<String>
    val columnToSortBy: Int
    fun dumpTable(): DumperTableData

    override fun dumpData(reporter: DumpProgressReporter): List<DumpOutput> {
        val count = getCount()
        val dumpFile = CraftDumper.getOutputFile(registryName!!, ".csv")
        dumpFile.parentFile.mkdirs()
        dumpFile.printWriter().use { writer ->
            with(writer) {
                appendCsvRow(header)
                dumpTable().forEachIndexed { index, row ->
                    appendln()
                    appendCsvRow(row)
                    reporter.progress = (index + 1F) / count
                }
            }
        }
        return listOf(DumpOutput("table", dumpFile))
    }
}

typealias DumperTableCreator = suspend SequenceScope<List<String>>.() -> Unit

class DumperTableContext : DumperBase(), DumperTable {
    private lateinit var tableCreator: DumperTableCreator
    override lateinit var header: List<String>
    override var columnToSortBy = 0

    override fun dumpTable(): DumperTableData = Iterable { iterator(tableCreator) }

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
        catch (e: Throwable) {
            createAddError(e)
        }
    )
}

@PublishedApi
internal fun MutableList<String>.createAddError(e: Throwable): String {
    CraftDumper.log.error("An error occurred while creating a dump! Already dumped data: ${joinToString(prefix = "[", separator = ", ", postfix = "]")}", e)
    return "*error*"
}

suspend inline fun SequenceScope<List<String>>.row(size: Int, block: MutableList<String>.() -> Unit) {
    val lst = ArrayList<String>(size)
    try {
        lst.apply(block)
    }
    catch(e: Throwable) {
        lst.createAddError(e)
    }
    yield(lst)
}