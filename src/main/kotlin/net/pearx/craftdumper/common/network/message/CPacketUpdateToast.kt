package net.pearx.craftdumper.common.network.message

import net.minecraft.client.Minecraft
import net.minecraft.network.PacketBuffer
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fml.network.NetworkEvent
import net.pearx.craftdumper.client.DumperToast
import net.pearx.craftdumper.common.helper.internal.network.PacketHandler
import net.pearx.craftdumper.common.helper.internal.network.readTextComponentNullable
import net.pearx.craftdumper.common.helper.internal.network.writeTextComponentNullable


data class CPacketUpdateToast(var token: Int, var progress: Float, var title: ITextComponent? = null, var subtitle: ITextComponent? = null) {
    companion object : PacketHandler<CPacketUpdateToast> {
        override fun encode(msg: CPacketUpdateToast, buf: PacketBuffer) {
            buf.writeInt(msg.token)
            buf.writeFloat(msg.progress)
            buf.writeTextComponentNullable(msg.title)
            buf.writeTextComponentNullable(msg.subtitle)
        }

        override fun decode(buf: PacketBuffer): CPacketUpdateToast {
            return CPacketUpdateToast(buf.readInt(), buf.readFloat(), buf.readTextComponentNullable(), buf.readTextComponentNullable())
        }

        override fun handle(msg: CPacketUpdateToast, ctx: () -> NetworkEvent.Context) {
            ctx().enqueueWork {
                val token = msg.token
                val progress = msg.progress
                val title = msg.title
                val subtitle = msg.subtitle
                val gui = Minecraft.getInstance().toastGui
                val toast = gui.getToast(DumperToast::class.java, token)
                if (toast != null) {
                    toast.progress = progress
                    if (title != null)
                        toast.title = title
                    if (subtitle != null)
                        toast.subtitle = subtitle
                }
            }
        }

    }
}