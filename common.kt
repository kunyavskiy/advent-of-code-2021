@OptIn(ExperimentalStdlibApi::class)
fun readAllLines() = buildList {
    while (true) {
        add(readLine() ?: break)
    }
}

fun String.splitNonEmpty(delimiters: String = " ") = split(delimiters).filterNot { it.isEmpty() }
fun String.splitDigits() = replace(Regex("[^0-9]"), " ").splitNonEmpty()
fun String.splitInts() = replace(Regex("[^0-9-]"), " ").splitNonEmpty().map { it.toInt() }

fun <T> Sequence<T>.takeUntil(predicate: (T) -> Boolean) = sequence {
    for (i in this@takeUntil) {
        yield(i)
        if (predicate(i)) break
    }
}

fun <T> List<T>.replace(index: Int, value: T) = mapIndexed { i, v ->
    if (i != index) v else value
}
