package net.pearx.craftdumper.common.network

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side
import net.pearx.craftdumper.ID
import net.pearx.craftdumper.common.network.message.CPacketCreateToast
import net.pearx.craftdumper.common.network.message.CPacketHideToast
import net.pearx.craftdumper.common.network.message.CPacketUpdateToast

val NETWORK: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ID)

fun initNetwork() {
    with(NETWORK) {
        var id = 0
        registerMessage(CPacketCreateToast.Handler::class.java, CPacketCreateToast::class.java, id++, Side.CLIENT)
        registerMessage(CPacketUpdateToast.Handler::class.java, CPacketUpdateToast::class.java, id++, Side.CLIENT)
        registerMessage(CPacketHideToast.Handler::class.java, CPacketHideToast::class.java, id, Side.CLIENT)
    }
}