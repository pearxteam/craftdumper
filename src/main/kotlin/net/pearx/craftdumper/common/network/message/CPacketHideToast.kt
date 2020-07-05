package net.pearx.craftdumper.common.network.message

import net.minecraft.client.Minecraft
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import net.pearx.craftdumper.client.DumperToast
import net.pearx.craftdumper.common.helper.internal.PacketHandler


data class CPacketHideToast(var token: Int) {

    companion object : PacketHandler<CPacketHideToast> {
        override fun encode(msg: CPacketHideToast, buf: PacketBuffer) {
            buf.writeInt(msg.token)
        }

        override fun decode(buf: PacketBuffer): CPacketHideToast {
            return CPacketHideToast(buf.readInt())
        }

        override fun handle(msg: CPacketHideToast, ctx: () -> NetworkEvent.Context) {
            ctx().enqueueWork {
                val toast = Minecraft.getInstance().toastGui.getToast(DumperToast::class.java, msg.token)
                toast?.hide()
            }
        }
    }
}