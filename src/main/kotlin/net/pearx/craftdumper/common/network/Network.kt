package net.pearx.craftdumper.common.network

import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.SimpleChannel
import net.pearx.craftdumper.common.helper.internal.craftdumper
import net.pearx.craftdumper.common.helper.internal.register
import net.pearx.craftdumper.common.network.message.CPacketCreateToast
import net.pearx.craftdumper.common.network.message.CPacketHideToast
import net.pearx.craftdumper.common.network.message.CPacketUpdateToast

private const val PROTOCOL_VERSION = "1"

val NETWORK: SimpleChannel = NetworkRegistry.newSimpleChannel(craftdumper("main"), { PROTOCOL_VERSION }, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals)

fun initNetwork() {
    with(NETWORK) {
        var id = 0
        register(id++, CPacketCreateToast)
        register(id++, CPacketHideToast)
        register(id, CPacketUpdateToast)
    }
}