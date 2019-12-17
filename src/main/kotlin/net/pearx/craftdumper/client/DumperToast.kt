package net.pearx.craftdumper.client

import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.toasts.GuiToast
import net.minecraft.client.gui.toasts.IToast
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftdumper.dumper.DumpOutputType
import net.pearx.craftdumper.dumper.Dumper

@SideOnly(Side.CLIENT)
class DumperToast(dumper: Dumper, type: DumpOutputType) : IToast {
    private val title: String = I18n.format("craftdumper.toast.title", dumper.getTextComponent().formattedText)
    private val subtitle = I18n.format("craftdumper.toast.subtitle.${type.value}")
    private val hasProgress = type.hasProgress
    private var visibility = IToast.Visibility.SHOW
    private var lastDelta: Long = 0
    private var displayedProgress = 0f
    @Volatile
    var progress = 0F

    override fun draw(toastGui: GuiToast, delta: Long): IToast.Visibility {
        toastGui.minecraft.textureManager.bindTexture(IToast.TEXTURE_TOASTS)
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        toastGui.drawTexturedModalRect(0, 0, 0, 96, 160, 32)
        GlStateManager.enableBlend()
        toastGui.drawTexturedModalRect(6, 6, 176, 20, 20, 20)
        GlStateManager.enableBlend()
        toastGui.minecraft.fontRenderer.drawString(title, 30, 7, -11534256)
        toastGui.minecraft.fontRenderer.drawString(subtitle, 30, 18, -16777216)
        if (hasProgress) {
            Gui.drawRect(3, 28, 157, 29, -1)
            val f = MathHelper.clampedLerp(displayedProgress.toDouble(), progress.toDouble(), ((delta - lastDelta).toFloat() / 100.0f).toDouble()).toFloat()
            val i: Int
            i = if (progress >= displayedProgress) {
                -16755456
            }
            else {
                -11206656
            }
            Gui.drawRect(3, 28, (3.0f + 154.0f * f).toInt(), 29, i)
            displayedProgress = f
            lastDelta = delta
        }
        return visibility
    }

    fun hide() {
        visibility = IToast.Visibility.HIDE
    }
}