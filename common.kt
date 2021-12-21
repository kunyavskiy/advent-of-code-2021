@OptIn(ExperimentalStdlibApi::class)
fun readAllLines() = buildList {
    while (true) {
        add(readLine() ?: break)
    }
}

fun String.splitNonEmpty(delimiters: String = " ") = split(delimiters).filterNot { it.isEmpty() }
fun String.splitDigits() = replace(Regex("[^0-9]"), " ").splitNonEmpty()

fun <T> Sequence<T>.takeUntil(predicate: (T) -> Boolean) = sequence {
    for (i in this@takeUntil) {
        yield(i)
        if (predicate(i)) break
    }
}