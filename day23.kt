import kotlin.math.*

private val Int.needId get() = when (this) {
    1 -> 2
    10 -> 4
    100 -> 6
    1000 -> 8
    else -> TODO()
}

private data class Day23State(val a:List<List<Int?>>, val c:List<Int?>) {
    fun final() = c.all { it == null } &&
            a.indices.all { index -> a[index].all{ it != null && it.needId == index } }

    fun moveFromC(index: Int) : Pair<Day23State, Int>? {
        val to = c[index]?.needId ?: return null
        val top = a[to].indexOfFirst { it == null }.takeIf { it >= 0 } ?: return null
        return when {
            a[to].any { it != null && it.needId != to } -> null
            index < to && (index + 1 until to).any { c[it] != null } -> null
            index > to && (to + 1 until index).any { c[it] != null } -> null
            else -> Day23State(
                a.replace(to, a[to].replace(top, c[index])),
                c.replace(index, null)
            ) to (c[index]!! * (a[to].size - top + abs(index - to)))
        }
    }

    fun moveToC(from: Int, index:Int): Pair<Day23State, Int>? {
        val top = a[from].indexOfLast { it != null }.takeIf { it >= 0 } ?: return null
        return when {
            c[index] != null -> null
            a[from].all { it == null || it.needId == from } -> null
            index < from && (index + 1 until from).any { c[it] != null } -> null
            index > from && (from + 1 until index).any { c[it] != null } -> null
            else -> Day23State(
                a.replace(from, a[from].replace(top, null)),
                c.replace(index, a[from][top])
            ) to (a[from][top]!! * (abs(index - from) + a[from].size - top))
        }
    }
}

fun main() {
    val easyData = Day23State(
        listOf(
            emptyList(),
            emptyList(),
            listOf(1000, 1),
            emptyList(),
            listOf(1, 100),
            emptyList(),
            listOf(1000, 10),
            emptyList(),
            listOf(10, 100),
            emptyList(),
            emptyList()
        ),
        arrayOfNulls<Int?>(11).toList()
    )
    val hardData = Day23State(
        listOf(
            emptyList(),
            emptyList(),
            listOf(1000, 1000, 1000, 1),
            emptyList(),
            listOf(1, 10, 100, 100),
            emptyList(),
            listOf(1000, 1, 10, 10),
            emptyList(),
            listOf(10, 100, 1, 100),
            emptyList(),
            emptyList()
        ),
        arrayOfNulls<Int?>(11).toList()
    )

    println(solve(easyData))
    cache.clear()
    println(solve(hardData))
}

private val cache = mutableMapOf<Any, Int>()

private val fromList = listOf(0, 1, 3, 5, 7, 9, 10)
private val toList = fromList.flatMap { f -> listOf(2, 4, 6, 8).map { it to f } }

private fun solve(state: Day23State) : Int = cache.getOrPut(state) {
    if (state.final())
        0
    else
        fromList
            .mapNotNull { state.moveFromC(it) }
            .minOfOrNull { it.second + solve(it.first) }
            ?: toList
                .mapNotNull { state.moveToC(it.first, it.second) }
                .minOfOrNull { it.second + solve(it.first) }
            ?: (Int.MAX_VALUE / 2)
}