package net.pearx.craftdumper.dumper

import net.pearx.craftdumper.helper.client
import java.io.InputStream
import kotlin.random.Random

data class DumpFile(val path: String, val contents: InputStream)

interface DumperFiles : Dumper {
    fun dumpFiles(): Iterable<DumpFile>

    override fun dumpContents() {
        // TODO: implement
    }
}

typealias DumperFilesCreator = suspend SequenceScope<DumpFile>.() -> Unit

class DumperFilesContext : DumperBase(), DumperFiles {
    private lateinit var filesCreator: DumperFilesCreator

    override fun dumpFiles(): Iterable<DumpFile> = Iterable { iterator(filesCreator) }

    fun files(block: DumperFilesCreator) {
        filesCreator = block
    }
}

inline fun dumperFiles(init: DumperFilesContext.() -> Unit): DumperFiles = DumperFilesContext().apply(init)

inline fun dumperFilesClient(init: DumperFilesContext.() -> Unit): DumperFiles? = client { DumperFilesContext().apply(init) }

suspend inline fun SequenceScope<DumpFile>.file(getPath: () -> String, getContents: () -> InputStream) {
    val path = try { getPath() } catch(e: Exception) { "error_${Random.nextInt()}" }
    val contents = try { getContents() } catch(e: Exception) { ByteArray(0).inputStream() }
    yield(DumpFile(path, contents))
}