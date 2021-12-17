import kotlin.math.*

fun main() {
    println(easy(20..30, -10..-5))
    println(easy(128..160, -142..-88))
    println(hard(20..30, -10..-5))
    println(hard(128..160, -142..-88))
}

data class State(val x: Int, val y: Int, val dx: Int, val dy: Int)

private fun getVariants(tx: IntRange, ty: IntRange) = (0..tx.last).flatMap { dx ->
    (ty.first..-2 * ty.last).mapNotNull { dy ->
        generateSequence(State(0, 0, dx, dy)) {
            State(it.x + it.dx, it.y + it.dy, max(0, it.dx - 1), it.dy - 1)
        }.takeWhile {
            it.x <= tx.last && it.y >= ty.first
        }.toList().takeIf {
            it.last().x in tx && it.last().y in ty
        }
    }
}

private fun easy(tx: IntRange, ty: IntRange) = getVariants(tx, ty).maxOf {
    it.maxOf { it.y }
}

private fun hard(tx: IntRange, ty: IntRange) = getVariants(tx, ty).count()