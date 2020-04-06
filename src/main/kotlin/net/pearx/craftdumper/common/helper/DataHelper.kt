package net.pearx.craftdumper.common.helper

import net.minecraft.util.ResourceLocation
import org.apache.commons.lang3.reflect.FieldUtils
import java.lang.reflect.Field
import kotlin.reflect.KClass

fun Boolean.toPlusMinusString() = if (this) "+" else "-"

fun Int.toHexColorString() = "#${Integer.toHexString(this).toUpperCase().padStart(6, '0')}"

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

//fun Class<*>.getOwnerModId(): String {
//    return Loader.instance().modList.firstOrNull { mod -> mod.ownedPackages.any {  ownedPackage ->
//        name.substringBeforeLast('.') == ownedPackage
//    } }?.modId ?: "unknown"
//}

//inline fun KClass<*>.getOwnerModId() = this.java.getOwnerModId()