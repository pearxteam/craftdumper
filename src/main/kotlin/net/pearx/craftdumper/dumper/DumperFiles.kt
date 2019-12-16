package net.pearx.craftdumper.dumper

import net.pearx.craftdumper.CraftDumper
import net.pearx.craftdumper.helper.client
import net.pearx.craftdumper.helper.currentDateTime
import net.pearx.craftdumper.helper.getRegistryElementName
import java.io.InputStream
import kotlin.random.Random

data class DumpFile(val path: String, val contents: InputStream)

typealias DumperFileContents = Iterable<DumpFile>

interface DumperFiles : Dumper {
    fun dumpFiles(): DumperFileContents

    override fun dumpContents(reporter: DumpProgressReporter): List<DumpOutput> {
        val count = getCount()
        val baseDirectory = CraftDumper.outputDirectory
            .resolve("${DumperRegistry.getRegistryElementName(registryName!!)}_${currentDateTime()}")
        dumpFiles().forEachIndexed { index, file ->
            val dumpPath = baseDirectory
                .resolve(file.path)
            dumpPath.parentFile.mkdirs()
            file.contents.use { dump ->
                dumpPath.outputStream().buffered().use { file ->
                    dump.copyTo(file)
                }
            }
            reporter.progress = (index + 1.0) / count
        }
        return listOf(DumpOutput("craftdumper.output.directory.name", baseDirectory))
    }
}

typealias DumperFilesCreator = suspend SequenceScope<DumpFile>.() -> Unit

class DumperFilesContext : DumperBase(), DumperFiles {
    private lateinit var filesCreator: DumperFilesCreator

    override fun dumpFiles(): DumperFileContents = Iterable { iterator(filesCreator) }

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