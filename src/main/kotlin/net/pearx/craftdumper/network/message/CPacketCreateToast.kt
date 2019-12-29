package net.pearx.craftdumper.network.message

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftdumper.client.DumperToast
import net.pearx.craftdumper.dumper.Dumper
import net.pearx.craftdumper.dumper.DumperRegistry
import net.pearx.craftdumper.helper.internal.readString
import net.pearx.craftdumper.helper.internal.writeString

class CPacketCreateToast(private var token: Int, private var dumper: Dumper?, private var subtitle: String?) : IMessage {
    constructor() : this(-1, null, null)

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(token)
        buf.writeString(dumper!!.registryName.toString())
        buf.writeString(subtitle!!)
    }

    override fun fromBytes(buf: ByteBuf) {
        token = buf.readInt()
        dumper = DumperRegistry.getValue(ResourceLocation(buf.readString()))
        subtitle = buf.readString()
    }

    class Handler : IMessageHandler<CPacketCreateToast, IMessage> {
        @SideOnly(Side.CLIENT)
        override fun onMessage(message: CPacketCreateToast, ctx: MessageContext): IMessage? {
            Minecraft.getMinecraft().addScheduledTask {
                Minecraft.getMinecraft().toastGui.add(DumperToast(message.token, message.dumper!!, message.subtitle!!))
            }
            return null
        }
    }
}