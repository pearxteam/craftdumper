@file:JvmMultifileClass
@file:JvmName("InternalHelper")

package net.pearx.craftdumper.common.helper.internal

import io.netty.buffer.ByteBuf
import net.minecraft.util.text.ITextComponent
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

internal fun ByteBuf.writeTextComponentNullable(component: ITextComponent?) {
    writeStringNullable(component?.let { ITextComponent.Serializer.componentToJson(it) })
}

internal fun ByteBuf.readStringNullable(): String? {
    val isNull = readBoolean()
    return if (isNull)
        null
    else
        readString()
}

internal fun ByteBuf.readTextComponentNullable(): ITextComponent? = readStringNullable()?.let { ITextComponent.Serializer.jsonToComponent(it) }