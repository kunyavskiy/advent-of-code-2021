import kotlin.math.*

fun main() {
    println(easy(intArrayOf(4, 8)))
    println(easy(intArrayOf(2, 5)))
    println(hard(intArrayOf(4, 8)))
    println(hard(intArrayOf(2, 5)))
}

private data class GameState(val position:Int, val score: Int) {
    fun move(value: Int) = GameState((position + value) % 10, score + (position + value) % 10 + 1)
}

private fun easy(p: IntArray) = generateSequence(1) { it + 1 }
    .chunked(3)
    .map { it.sum() }
    .runningFoldIndexed(Array(2) {GameState(p[it] - 1, 0) }) { index, acc, shift ->
        acc.clone().also { it[index % 2] = it[index % 2].move(shift) }
    }
    .takeUntil { it.any { it.score >= 1000 } }
    .withIndex()
    .last()
    .let { it.index * 3 * it.value.minOf { it.score } }

private val solveCache = mutableMapOf<Pair<GameState, GameState>, Pair<Long, Long>>()

private fun solve(a: GameState, b: GameState) : Pair<Long, Long> = solveCache.getOrPut(a to b) {
    if (b.score >= 21) {
        0L to 1L
    } else {
        Array(27) {
            solve(b, a.move(it / 9 + (it / 3) % 3 + it % 3 + 3))
        }.let { x -> x.sumOf { it.second } to x.sumOf { it.first } }
    }
}

private fun hard(p: IntArray) =
    solve(GameState(p[0] - 1, 0), GameState(p[1] - 1, 0))
        .let { max(it.first, it.second) }