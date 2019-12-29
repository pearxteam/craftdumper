package net.pearx.craftdumper.network

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import net.pearx.craftdumper.ID
import net.pearx.craftdumper.network.message.CPacketCreateToast
import net.pearx.craftdumper.network.message.CPacketHideToast
import net.pearx.craftdumper.network.message.CPacketUpdateToast


val CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(ID)

fun initNetwork() {
    with(CHANNEL) {
        var id = 0
        registerMessage(CPacketCreateToast.Handler::class.java, CPacketCreateToast::class.java, id++, Side.CLIENT)
        registerMessage(CPacketUpdateToast.Handler::class.java, CPacketUpdateToast::class.java, id++, Side.CLIENT)
        registerMessage(CPacketHideToast.Handler::class.java, CPacketHideToast::class.java, id, Side.CLIENT)
    }
}