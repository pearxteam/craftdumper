package net.pearx.craftdumper.common.command

val DEFAULT_DUMP_TYPE = DumpType.FULL

fun dumpTypeOf(value: String) = DumpType.values().firstOrNull { it.value == value }
val dumpTypes = DumpType.values().map { it.value }

enum class DumpType(val value: String, val data: Boolean = false, val amounts: Boolean = false) {
    FULL("full", data = true, amounts = true),
    AMOUNTS("amounts", amounts = true),
    DATA("data", data = true);
}