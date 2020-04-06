package net.pearx.craftdumper.client

import com.mojang.blaze3d.platform.GlStateManager.color3f
import com.mojang.blaze3d.platform.GlStateManager.enableBlend
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.toasts.IToast
import net.minecraft.client.gui.toasts.ToastGui
import net.minecraft.util.math.MathHelper.clampedLerp
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class DumperToast(private val token: Int, @Volatile var progress: Float = 0F, var title: ITextComponent? = null, var subtitle: ITextComponent? = null) : IToast {
    private var visibility = IToast.Visibility.SHOW
    private var lastDelta: Long = 0
    private var displayedProgress = 0f

    override fun draw(toastGui: ToastGui, delta: Long): IToast.Visibility {
        with(toastGui) {
            with(minecraft.textureManager) {
                with(minecraft.fontRenderer) {
                    bindTexture(IToast.TEXTURE_TOASTS)
                    color3f(1.0f, 1.0f, 1.0f)
                    toastGui.blit(0, 0, 0, 96, 160, 32)
                    enableBlend()
                    toastGui.blit(6, 6, 176, 20, 20, 20)
                    enableBlend()
                    drawString(title?.formattedText.orEmpty(), 30.0f, 7.0f, -11534256)
                    drawString(subtitle?.formattedText.orEmpty(), 30.0f, 18.0f, -16777216)

                    AbstractGui.fill(3, 28, 157, 29, -1)
                    val prog = clampedLerp(displayedProgress.toDouble(), progress.toDouble(), ((delta - lastDelta).toFloat() / 100.0f).toDouble()).toFloat()
                    val barColor = if (progress >= displayedProgress) -16755456 else -11206656
                    AbstractGui.fill(3, 28, (3.0f + 154.0f * prog).toInt(), 29, barColor)
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