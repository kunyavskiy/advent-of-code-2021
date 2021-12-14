fun main() {
    val data_ = readAllLines()
    val dataPoints = data_.takeWhile { it != "" }.map { it.splitDigits().let { it[0].toInt() to it[1].toInt() } }
    val dataFolds = data_.drop(dataPoints.size + 1).map {
        it.split(" ")[2].split("=").let {
            (it[0] == "x") to it[1].toInt()
        }
    }
    println(easy(dataPoints, dataFolds))
    println(hard(dataPoints, dataFolds))
}

fun fold(p: Pair<Int, Int>, vertical: Boolean, v: Int) = when {
    vertical && p.first >= v -> (2 * v - p.first) to p.second
    !vertical && p.second >= v -> p.first to (2 * v - p.second)
    else -> p
}

private fun easy(points: List<Pair<Int, Int>>, folds: List<Pair<Boolean, Int>>) = points.map {
    fold(it, folds[0].first, folds[0].second)
}.distinct().count()

private fun hard(points: List<Pair<Int, Int>>, folds: List<Pair<Boolean, Int>>) = folds.fold(points) { acc, f ->
    acc.map {
        fold(it, f.first, f.second)
    }.distinct()
}.let { pt ->
    Array(pt.maxOf { it.second + 1 }) { r ->
        Array(pt.maxOf { it.first + 1 }) { c ->
            if (c to r in pt) "#" else " "
        }.joinToString("")
    }.joinToString("\n")
}
