fun main() {
    val order = readLine()!!.split(",").map { it.toInt() }
    val boards = readAllLines().chunked(6).map { board ->
        board.drop(1).map { row ->
            row.splitNonEmpty().map { it.toInt() }
        }
    }
    println(easy(order, boards))
    println(hard(order, boards))
}

private fun winTime(order: List<Int>, board: List<List<Int>>) = order.indices.first { index ->
    board.any { r -> r.all { order.indexOf(it) <= index } } ||
            board[0].indices.any { c -> board.all { order.indexOf(it[c]) <= index } }
}

private fun score(order: List<Int>, board: List<List<Int>>, winTime: Int) =
    order[winTime] * board.sumOf { row -> row.filter { order.indexOf(it) > winTime }.sum() }

private fun easy(order: List<Int>, boards: List<List<List<Int>>>) = boards
    .map { board -> board to winTime(order, board) }
    .minByOrNull { it.second }!!
    .let { score(order, it.first, it.second) }

private fun hard(order: List<Int>, boards: List<List<List<Int>>>): Int = boards
    .map { board -> board to winTime(order, board) }
    .maxByOrNull { it.second }!!
    .let { score(order, it.first, it.second) }