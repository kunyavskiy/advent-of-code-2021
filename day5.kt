import kotlin.math.*

private data class Line(val x1:Int, val y1:Int, val x2:Int, val y2: Int) {
    fun on(x:Int, y:Int) =
        (x - x1) * (y2 - y1) - (y - y1) * (x2 - x1) == 0 &&
                x in (minX..maxX) && y in (minY..maxY)
    val minX get() = minOf(x1, x2)
    val minY get() = minOf(y1, y2)
    val maxX get() = maxOf(x1, x2)
    val maxY get() = maxOf(y1, y2)
}

fun main() {
    val data = readAllLines().map {
        val (x1, y1, x2, y2) = it.splitDigits().map { it.toInt() }
        Line(x1, y1, x2, y2)
    }
    println(easy(data))
    println(hard(data))
}

private fun countAnswer(lines: List<Line>) =
    (lines.minOf { it.minX } .. lines.maxOf { it.maxX }).sumOf { x ->
        (lines.minOf { it.minY } .. lines.maxOf { it.maxY }).count { y ->
            lines.count { it.on(x, y) } > 1
        }
    }


private fun easy(data: List<Line>) = data
    .filter { it.x1 == it.x2 || it.y1 == it.y2 }
    .let { countAnswer(it) }


private fun hard(data: List<Line>) = data
    .filter { it.x1 == it.x2 || it.y1 == it.y2 || (it.x1 - it.x2).absoluteValue == (it.y1 - it.y2).absoluteValue }
    .let { countAnswer(it) }
