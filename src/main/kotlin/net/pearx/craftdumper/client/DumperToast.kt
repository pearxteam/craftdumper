package net.pearx.craftdumper.client

import net.minecraft.client.gui.Gui.drawRect
import net.minecraft.client.gui.toasts.GuiToast
import net.minecraft.client.gui.toasts.IToast
import net.minecraft.client.renderer.GlStateManager.color
import net.minecraft.client.renderer.GlStateManager.enableBlend
import net.minecraft.client.resources.I18n
import net.minecraft.util.math.MathHelper.clampedLerp
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftdumper.dumper.Dumper

@SideOnly(Side.CLIENT)
class DumperToast(dumper: Dumper, subtitle: String, private val hasProgress: Boolean) : IToast {
    private val title: String = I18n.format("craftdumper.toast.title", dumper.getTextComponent().formattedText)
    private lateinit var subtitleText: String
    private var visibility = IToast.Visibility.SHOW
    private var lastDelta: Long = 0
    private var displayedProgress = 0f

    @Volatile
    var progress = 0F

    var subtitle: String = subtitle
        set(value) {
            field = value
            subtitleText = I18n.format("craftdumper.toast.subtitle.$subtitle")
        }

    init {
        this.subtitle = subtitle
    }

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
                    drawString(title, 30, 7, -11534256)
                    drawString(subtitleText, 30, 18, -16777216)
                    if (hasProgress) {
                        drawRect(3, 28, 157, 29, -1)
                        val prog = clampedLerp(displayedProgress.toDouble(), progress.toDouble(), ((delta - lastDelta).toFloat() / 100.0f).toDouble()).toFloat()
                        val barColor = if (progress >= displayedProgress) -16755456 else -11206656
                        drawRect(3, 28, (3.0f + 154.0f * prog).toInt(), 29, barColor)
                        displayedProgress = prog
                        lastDelta = delta
                    }
                }
            }
        }
        return visibility
    }

    fun hide() {
        visibility = IToast.Visibility.HIDE
    }
}