fun main() {
    val data = readAllLines()
    println(easy(data))
    println(hard(data))
}

private val dirs = listOf(0 to -1, 0 to 1, 1 to 0, -1 to 0)

private fun easy(data: List<String>): Int {
    val d = Array(data.size) { IntArray(data[0].length) { Int.MAX_VALUE } }
    val q = mutableListOf<Pair<Int, Int>>()
    d[0][0] = 0
    q.add(0 to 0)
    var qid = 0
    while (qid < q.size) {
        val (x, y) = q[qid++]
        for ((dx, dy) in dirs) {
            val xx = x + dx
            val yy = y + dy
            if (xx in d.indices && yy in d[x].indices && d[xx][yy] > d[x][y] + data[xx][yy].digitToInt()) {
                d[xx][yy] = d[x][y] + data[xx][yy].digitToInt()
                q.add(xx to yy)
            }
        }
    }
    return d.last().last()
}

private fun hard(data: List<String>) =
    easy(List(data.size * 5) { x ->
        IntArray(data[0].length * 5) { y ->
            (data[x % data.size][y % data[0].length].digitToInt() + (x / data.size) + (y / data[0].length) - 1) % 9 + 1
        }.joinToString("")
    })
