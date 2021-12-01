@file:OptIn(ExperimentalStdlibApi::class)

fun readAllLines() = buildList {
    while (true) {
        add(readLine() ?: break)
    }
}