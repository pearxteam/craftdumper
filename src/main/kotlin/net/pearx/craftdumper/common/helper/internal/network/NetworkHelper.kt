@file:JvmMultifileClass
@file:JvmName("InternalHelper")

package net.pearx.craftdumper.common.helper.internal.network

import net.minecraft.network.PacketBuffer
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.simple.SimpleChannel

internal interface PacketHandler<T> {
    fun encode(msg: T, buf: PacketBuffer)
    fun decode(buf: PacketBuffer): T
    fun handle(msg: T, ctx: () -> NetworkEvent.Context)
}

internal inline fun <reified T> SimpleChannel.register(id: Int, serializer: PacketHandler<T>) {
    registerMessage(id, T::class.java, { msg: T, buffer: PacketBuffer -> serializer.encode(msg, buffer) }, { buffer: PacketBuffer -> serializer.decode(buffer) }, { msg, ctx -> serializer.handle(msg, { ctx.get() }) })
}

internal fun PacketBuffer.writeTextComponentNullable(component: ITextComponent?) {
    writeBoolean(component == null)
    if (component != null)
        writeTextComponent(component)
}

internal fun PacketBuffer.readTextComponentNullable(): ITextComponent? {
    val isNull = readBoolean()
    return if (isNull) null else readTextComponent()
}