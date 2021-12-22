data class Query(val on:Boolean, val x: IntRange, val y: IntRange, val z:IntRange)

private fun IntRange.cut() = maxOf(first, -50)..minOf(last, 50)

fun main() {
    val data = readAllLines().map { it.split(" ").let {
        val on = it[0] == "on"
        val vals = it[1].splitInts()
        Query(on, vals[0]..vals[1], vals[2]..vals[3], vals[4]..vals[5])
    } }
    println(data)
    println(easy(data))
    println(hard(data))
}

private fun easy(q: List<Query>) = hard(q.map { Query(it.on, it.x.cut(), it.y.cut(), it.z.cut()) })
private fun hard(q: List<Query>) : Long {
    val xc = q.flatMap { listOf(it.x.first, it.x.last + 1) }.distinct().sorted()
    val yc = q.flatMap { listOf(it.y.first, it.y.last + 1) }.distinct().sorted()
    val zc = q.flatMap { listOf(it.z.first, it.z.last + 1) }.distinct().sorted()
    fun IntRange.remap(x: List<Int>) = x.binarySearch(first) until x.binarySearch(last + 1)
    val cq = q.map {
        Query(it.on, it.x.remap(xc), it.y.remap(yc), it.z.remap(zc))
    }
    return getSet(cq).withIndex().sumOf { (x, onsx) ->
        onsx.withIndex().sumOf { (y, onsxy) ->
            onsxy.withIndex().sumOf { (z, isOn) ->
                if (isOn) {
                    (xc[x + 1] - xc[x]).toLong() *
                            (yc[y + 1] - yc[y]).toLong() *
                            (zc[z + 1] - zc[z]).toLong()
                } else 0L
            }
        }
    }
}

private fun getSet(q: List<Query>) : Array<Array<BooleanArray>> {
    val ons = Array(q.maxOf { it.x.last + 1 }) {
        Array(q.maxOf { it.y.last + 1 }) {
            BooleanArray(q.maxOf { it.z.last + 1 }) {
                false
            }
        }
    }
    for ((on, xs, ys, zs) in q) {
        for (x in xs) for (y in ys) for (z in zs) {
            ons[x][y][z] = on
        }
    }
    return ons
}

