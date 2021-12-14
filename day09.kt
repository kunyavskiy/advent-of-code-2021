fun main() {
    val data = readAllLines()
    println(easy(data))
    println(hard(data))
}

private val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

@OptIn(ExperimentalStdlibApi::class)
private fun lowPoints(data: List<String>) = buildList {
    data.indices.forEach { x ->
        data[x].indices.forEach { y ->
            (x to y).takeIf {
                directions.all { (dx, dy) ->
                    data[x][y] < (data.getOrNull(x + dx)?.getOrNull(y + dy) ?: Char.MAX_VALUE)
                }
            }?.apply {
                add(this)
            }
        }
    }
}

private fun easy(data: List<String>) = lowPoints(data).sumOf { data[it.first][it.second].code - '0'.code + 1 }

private fun dfs(x: Int, y: Int, mark: Pair<Int, Int>, used: Array<Array<Pair<Int, Int>?>>, data: List<String>): Int =
    when {
        x !in data.indices -> 0
        y !in data[0].indices -> 0
        used[x][y] == mark -> 0
        data[x][y] == '9' -> 0
        used[x][y] != null -> throw AssertionError()
        else -> {
            used[x][y] = mark
            directions.sumOf { (dx, dy) -> dfs(x + dx, y + dy, mark, used, data) } + 1
        }
    }

private fun hard(data: List<String>) =
    Array(data.size) { Array<Pair<Int, Int>?>(data[0].length) { null } }.let { used ->
        lowPoints(data).map { dfs(it.first, it.second, it, used, data) }.sortedDescending().take(3).reduce(Int::times)
    }