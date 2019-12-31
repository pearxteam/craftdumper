package net.pearx.craftdumper.client

import net.minecraft.client.gui.Gui.drawRect
import net.minecraft.client.gui.toasts.GuiToast
import net.minecraft.client.gui.toasts.IToast
import net.minecraft.client.renderer.GlStateManager.color
import net.minecraft.client.renderer.GlStateManager.enableBlend
import net.minecraft.util.math.MathHelper.clampedLerp
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class DumperToast(private val token: Int, @Volatile var progress: Float = 0F, var title: ITextComponent? = null, var subtitle: ITextComponent? = null) : IToast {
    private var visibility = IToast.Visibility.SHOW
    private var lastDelta: Long = 0
    private var displayedProgress = 0f

    override fun draw(toastGui: GuiToast, delta: Long): IToast.Visibility {
        with(toastGui) {
            with(minecraft.textureManager) {
                with(minecraft.fontRenderer) {
                    bindTexture(IToast.TEXTURE_TOASTS)
                    color(1.0f, 1.0f, 1.0f)
                    drawTexturedModalRect(0, 0, 0, 96, 160, 32)
                    enableBlend()
                    drawTexturedModalRect(6, 6, 176, 20, 20, 20)
                    enableBlend()
                    drawString(title?.formattedText.orEmpty(), 30, 7, -11534256)
                    drawString(subtitle?.formattedText.orEmpty(), 30, 18, -16777216)

                    drawRect(3, 28, 157, 29, -1)
                    val prog = clampedLerp(displayedProgress.toDouble(), progress.toDouble(), ((delta - lastDelta).toFloat() / 100.0f).toDouble()).toFloat()
                    val barColor = if (progress >= displayedProgress) -16755456 else -11206656
                    drawRect(3, 28, (3.0f + 154.0f * prog).toInt(), 29, barColor)
                    displayedProgress = prog
                    lastDelta = delta
                }
            }
        }
        return visibility
    }

    fun hide() {
        visibility = IToast.Visibility.HIDE
    }

    override fun getType(): Any = token
}