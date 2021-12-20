fun IntRange.expand() = first-1..last+1

class Picture(val out: Boolean, val pos: Set<Pair<Int, Int>>) {
    val xs = (pos.minOf { it.first })..(pos.maxOf { it.first })
    val ys = (pos.minOf { it.second })..(pos.maxOf { it.second })
    private fun get(x: Int, y: Int) = if (x in xs && y in ys) pos.contains(x to y) else out
    private fun around(x: Int, y: Int) = BooleanArray(9) { get(x + it / 3 - 1, y + it % 3 - 1) }.toInt()

    fun process(algo: Set<Int>) = Picture(
        algo.contains(around(xs.first - 10, ys.first - 10)),
        xs.expand().flatMap { x ->
            ys.expand()
                .filter { algo.contains(around(x, it)) }
                .map { x to it }
        }.toSet()
    )

    fun count() = pos.size.also { require(!out) }
}

fun main() {
    val data = readAllLines()
    println(solve(data[0].toSharpList().toSet(), data.drop(2), 2))
    println(solve(data[0].toSharpList().toSet(), data.drop(2), 50))
}

fun BooleanArray.toInt() = fold(0) { acc, b -> acc * 2 + if (b) 1 else 0 }
fun String.toSharpList() = indices.filter { get(it) == '#' }

private fun solve(algo:Set<Int>, data: List<String>, count: Int) = generateSequence(Picture(false, data.flatMapIndexed { index, s ->
    s.toSharpList().map { index to it }
}.toSet())) { it.process(algo) }.drop(count).first().count()