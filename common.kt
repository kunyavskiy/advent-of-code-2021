@file:OptIn(ExperimentalStdlibApi::class)

fun readAllLines() = buildList {
    while (true) {
        add(readLine() ?: break)
    }
}

fun String.splitNonEmpty() = split(" ").filterNot { it.isEmpty() }