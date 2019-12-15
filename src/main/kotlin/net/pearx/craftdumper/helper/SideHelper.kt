package net.pearx.craftdumper.helper

import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side

val isClient = FMLCommonHandler.instance().side == Side.CLIENT

fun <T> ifOrNull(bool: Boolean, value: T) = if (bool) value else null
inline fun <T> ifOrNull(bool: Boolean, func: () -> T) = if (bool) func() else null

fun <T> client(value: T) = ifOrNull(isClient, value)
inline fun <T> client(func: () -> T) = ifOrNull(isClient, func)