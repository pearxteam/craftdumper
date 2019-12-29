package net.pearx.craftdumper.common.network.message

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftdumper.client.DumperToast
import net.pearx.craftdumper.common.helper.internal.readStringNullable
import net.pearx.craftdumper.common.helper.internal.writeStringNullable


class CPacketUpdateToast(var token: Int, var progress: Float, var subtitle: String? = null) : IMessage {
    constructor() : this(-1, 1F, null)

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(token)
        buf.writeFloat(progress)
        buf.writeStringNullable(subtitle)
    }

    override fun fromBytes(buf: ByteBuf) {
        token = buf.readInt()
        progress = buf.readFloat()
        subtitle = buf.readStringNullable()
    }

    class Handler : IMessageHandler<CPacketUpdateToast, IMessage> {
        @SideOnly(Side.CLIENT)
        override fun onMessage(message: CPacketUpdateToast, ctx: MessageContext): IMessage? {
            Minecraft.getMinecraft().addScheduledTask {
                val toast = Minecraft.getMinecraft().toastGui.getToast(DumperToast::class.java, message.token)
                if(toast != null) {
                    toast.progress = message.progress
                    message.subtitle?.also {
                        toast.subtitle = it
                    }
                }
            }
            return null
        }
    }
}