fun main() {
    val data = readAllLines().map { it.map { it.digitToInt() } }
    println(easy(data))
    println(hard(data))
}

private fun iterate(data: List<List<Int>>) =
    generateSequence(data.map { r -> r.map { it + 1 } }) { field ->
        field.mapIndexed { r, row ->
            row.mapIndexed { c, it ->
                if (it >= 10) 11 else
                    minOf(10, it + (-1..1).sumOf { dr ->
                        (-1..1).count { dc ->
                            field.getOrNull(r + dr)?.getOrNull(c + dc) == 10
                        }
                    })
            }
        }
    }.dropWhile { field -> field.any { r -> r.any(10::equals) } }
        .first()
        .map { r -> r.map { if (it == 11) 0 else it } }

private fun easy(data: List<List<Int>>) =
    generateSequence(data) { iterate(it) }
        .drop(1)
        .take(100)
        .sumOf { it.sumOf { r -> r.count(0::equals) } }

private fun hard(data: List<List<Int>>) =
    generateSequence(data) { iterate(it) }
        .takeWhile { it.any { r -> !r.all(0::equals) } }
        .count()