package net.pearx.craftdumper.dumper

import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.helper.client

interface DumperTable : Dumper {
    val header: List<String>
    val columnToSortBy: Int
    fun dumpTable(): Iterable<List<String>>

    override fun dumpContents() {
        // TODO: implement
    }
}

typealias DumperTableCreator = suspend SequenceScope<List<String>>.() -> Unit

class DumperTableContext : DumperBase(), DumperTable {
    private lateinit var tableCreator: DumperTableCreator
    override lateinit var header: List<String>
    override var columnToSortBy = 0

    override fun dumpTable(): Iterable<List<String>> = Iterable { iterator(tableCreator) }

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