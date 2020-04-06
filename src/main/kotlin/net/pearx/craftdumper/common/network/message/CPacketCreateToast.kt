package net.pearx.craftdumper.common.network.message

import net.minecraft.client.Minecraft
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import net.pearx.craftdumper.client.DumperToast
import net.pearx.craftdumper.common.helper.internal.network.PacketHandler

data class CPacketCreateToast(private var token: Int)  {
    companion object : PacketHandler<CPacketCreateToast> {
        override fun encode(msg: CPacketCreateToast, buf: PacketBuffer) {
            buf.writeInt(msg.token)
        }

        override fun decode(buf: PacketBuffer): CPacketCreateToast {
            return CPacketCreateToast(buf.readInt())
        }

        override fun handle(msg: CPacketCreateToast, ctx: () -> NetworkEvent.Context) {
            ctx().enqueueWork {
                Minecraft.getInstance().toastGui.add(DumperToast(msg.token))
            }
        }
    }
}