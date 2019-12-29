package net.pearx.craftdumper.helper.internal

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.ByteBufUtils

internal fun ByteBuf.writeString(string: String) {
    ByteBufUtils.writeUTF8String(this, string)
}

internal fun ByteBuf.readString(): String = ByteBufUtils.readUTF8String(this)

internal fun ByteBuf.writeStringNullable(string: String?) {
    writeBoolean(string == null)
    if (string != null)
        writeString(string)
}

internal fun ByteBuf.readStringNullable(): String? {
    val isNull = readBoolean()
    return if (isNull)
        null
    else
        readString()
}