package net.pearx.craftdumper.common.helper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.ModList
import net.minecraftforge.forgespi.language.ModFileScanData
import org.apache.commons.lang3.reflect.FieldUtils
import org.objectweb.asm.Type
import java.lang.reflect.Field
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.reflect.KClass

fun Boolean.toPlusMinusString() = if (this) "+" else "-"

fun Int.toHexColorString() = "#${Integer.toHexString(this).uppercase().padStart(6, '0')}"

fun ResourceLocation.toAssetsPath(topPath: String = "", postfix: String = "") = "assets/$namespace/$topPath${if (topPath.isEmpty()) "" else "/"}$path$postfix"

fun ResourceLocation.toTexturesPath(pngPostfix: Boolean = true) = toAssetsPath("textures", if (pngPostfix) ".png" else "")

fun <T> mutableListOfNotNull(vararg elements: T?): MutableList<T> {
    val lst = ArrayList<T>(elements.size)
    for (element in elements)
        if (element != null)
            lst.add(element)
    return lst
}

private val fieldCache = hashMapOf<String, Field?>()

@PublishedApi
internal fun readFieldRaw(clazz: Class<*>, reciever: Any?, vararg names: String): Any {
    for (name in names) {
        val field = run {
            val key = "${clazz.name}.$name"
            if (key in fieldCache) // can't use getOrPut here as of values can be null
                fieldCache[key]
            else {
                FieldUtils.getField(clazz, name, true).also { fieldCache[key] = it }
            }
        }
        if (field != null) {
            return field.get(reciever)
        }
    }
    throw NoSuchFieldException("No field with names ${names.contentToString()} found in ${clazz}.")
}

inline fun <reified T> Any.readField(vararg names: String): T = readFieldRaw(this::class.java, this, *names) as T

inline fun <reified T> KClass<*>.readField(vararg names: String): T = readFieldRaw(this.java, null, *names) as T

inline fun <reified T> Class<*>.readField(vararg names: String): T = readFieldRaw(this, null, *names) as T

fun Class<*>.getOwnerModId(): String {
    val classData = ModFileScanData.ClassData(Type.getType(this), null, null) // two nulls are here because ClassData#contains and ClassData#hashCode methods doesn't use these arguments
    return ModList.get().modFiles.firstOrNull { mod ->
        classData in mod.file.scanResult.classes
    }?.mods?.first()?.modId ?: "unknown"
}

inline fun KClass<*>.getOwnerModId() = this.java.getOwnerModId()

fun Float.toPercents(): Float = (this * 10000).roundToInt() / 100F
fun Float.round(digits: Int) = (this * (10F.pow(digits))).roundToInt() / (10F.pow(digits))
fun Double.round(digits: Int) = (this * (10.0.pow(digits))).roundToInt() / (10.0.pow(digits))