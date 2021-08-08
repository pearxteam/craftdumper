package net.pearx.craftdumper.common.dumper.dsl

import net.minecraft.util.ResourceLocation
import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.common.dumper.DumperTable
import net.pearx.craftdumper.common.helper.client

typealias DumperTableCreator = DumperTableDataContext.() -> Unit
typealias DumperTableHeaderCreator = () -> List<String>
typealias DumperTableDataFiller = suspend SequenceScope<List<String>>.() -> Unit

@DumperDsl
class DumperTableDataContext(val header: List<String>) {
    private lateinit var dataFiller: DumperTableDataFiller

    fun data(block: DumperTableDataFiller) {
        dataFiller = block
    }

    suspend inline fun SequenceScope<List<String>>.row(block: MutableList<String>.() -> Unit) {
        val lst = ArrayList<String>(header.size)
        try {
            lst.apply(block)
        }
        catch (e: Throwable) {
            lst.createAddError(e)
        }
        yield(lst)
    }

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

    @PublishedApi
    internal fun createIterator() = Iterable { iterator { dataFiller() } }
}

@DumperDsl
class DumperTableContext : DumperContext() {
    private lateinit var tableCreator: DumperTableCreator
    private lateinit var headerCreator: DumperTableHeaderCreator

    fun table(block: DumperTableCreator) {
        tableCreator = block
    }

    var header: List<String>
        get() = throw NotImplementedError()
        set(value) {
            headerCreator = { value }
        }

    fun header(block: DumperTableHeaderCreator) {
        headerCreator = block
    }

    @PublishedApi
    internal fun create(): DumperTable = DumperTableImplementation(registryName, translationKey, amountsCreator, countCreator, tableCreator, headerCreator)
}

inline fun dumperTable(init: DumperTableContext.() -> Unit): DumperTable = DumperTableContext().apply(init).create()
inline fun dumperTableClient(init: DumperTableContext.() -> Unit): DumperTable? = client { dumperTable(init) }

class DumperTableImplementation(
    _registryName: ResourceLocation?,
    _translationKey: String?,
    amountsCreator: DumpAmountsCreator?,
    countCreator: DumpCountCreator?,
    private val tableCreator: DumperTableCreator,
    private val headerCreator: DumperTableHeaderCreator
) : DumperImplementation(_registryName, _translationKey, amountsCreator, countCreator), DumperTable {
    override fun createHeader(): List<String> = headerCreator()

    override fun dumpTable(header: List<String>): Iterable<List<String>> = DumperTableDataContext(header).also(tableCreator).createIterator()

}