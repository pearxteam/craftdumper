package net.pearx.craftdumper.common.helper

inline fun <T, reified R> Array<T>.mapArray(transform: (T) -> R): Array<R> {
    return Array(size) { index ->
        transform(this[index])
    }
}