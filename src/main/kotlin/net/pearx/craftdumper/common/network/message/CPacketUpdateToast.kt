package net.pearx.craftdumper.common.network.message

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftdumper.client.DumperToast
import net.pearx.craftdumper.common.helper.internal.readTextComponentNullable
import net.pearx.craftdumper.common.helper.internal.writeTextComponentNullable


class CPacketUpdateToast(var token: Int, var progress: Float, var title: ITextComponent? = null, var subtitle: ITextComponent? = null) : IMessage {
    constructor() : this(-1, 0F)

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(token)
        buf.writeFloat(progress)
        buf.writeTextComponentNullable(title)
        buf.writeTextComponentNullable(subtitle)
    }

    override fun fromBytes(buf: ByteBuf) {
        token = buf.readInt()
        progress = buf.readFloat()
        title = buf.readTextComponentNullable()
        subtitle = buf.readTextComponentNullable()
    }

    class Handler : IMessageHandler<CPacketUpdateToast, IMessage> {
        @SideOnly(Side.CLIENT)
        override fun onMessage(message: CPacketUpdateToast, ctx: MessageContext): IMessage? {
            Minecraft.getMinecraft().addScheduledTask {
                val token = message.token
                val progress = message.progress
                val title = message.title
                val subtitle = message.subtitle
                val gui = Minecraft.getMinecraft().toastGui
                val toast = gui.getToast(DumperToast::class.java, token)
                if (toast != null) {
                    toast.progress = progress
                    if (title != null)
                        toast.title = title
                    if (subtitle != null)
                        toast.subtitle = subtitle
                }
            }
            return null
        }
    }
}