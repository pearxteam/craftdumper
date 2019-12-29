package net.pearx.craftdumper.network.message

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftdumper.client.DumperToast


class CPacketHideToast(var token: Int) : IMessage {
    constructor() : this(-1)

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(token)
    }

    override fun fromBytes(buf: ByteBuf) {
        token = buf.readInt()
    }

    class Handler : IMessageHandler<CPacketHideToast, IMessage> {
        @SideOnly(Side.CLIENT)
        override fun onMessage(message: CPacketHideToast, ctx: MessageContext): IMessage? {
            Minecraft.getMinecraft().addScheduledTask {
                val toast = Minecraft.getMinecraft().toastGui.getToast(DumperToast::class.java, message.token)
                toast?.hide()
            }
            return null
        }
    }
}