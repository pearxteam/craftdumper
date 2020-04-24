package net.pearx.craftdumper.common.dumper

import net.pearx.craftdumper.CraftDumper
import java.io.File
import java.io.InputStream

sealed class DumpFile(val path: String) {
    abstract fun write(absolutePath: File)

    class Copying(path: String, val data: InputStream) : DumpFile(path) {
        override fun write(absolutePath: File) {
            data.use { dump ->
                absolutePath.outputStream().buffered().use { file ->
                    dump.copyTo(file)
                }
            }
        }
    }

    class Direct(path: String, val writer: (File) -> Unit) : DumpFile(path) {
        override fun write(absolutePath: File) {
            writer(absolutePath)
        }
    }
}

interface DumperFiles : Dumper {
    fun dumpFiles(): Iterable<DumpFile>

    override fun dumpData(reporter: DumpProgressReporter): List<DumpOutput> {
        val baseDirectory = CraftDumper.getOutputFile(registryName!!)
        dumpFiles().forEachIndexed { index, file ->
            val dumpPath = baseDirectory
                .resolve(file.path)
            dumpPath.parentFile.mkdirs()
            try {
                file.write(dumpPath)
            }
            catch (e: Throwable) {
                CraftDumper.log.error("An error occurred while creating a dump!", e)
            }
            reporter.progress = index + 1
        }
        return listOf(DumpOutput("directory", baseDirectory))
    }
}