import kotlin.math.*

data class Point(val x:Int, val y:Int, val z :Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y, z - other.z)
}

val rotations = listOf(
    Triple(Point::x, Point::y, Point::z),
    Triple(Point::x, Point::z, Point::y),
    Triple(Point::y, Point::x, Point::z),
    Triple(Point::y, Point::z, Point::x),
    Triple(Point::z, Point::x, Point::y),
    Triple(Point::z, Point::y, Point::x),
)

data class Scanner(val lst: MutableList<Point>) {
    lateinit var position: Point
    fun merge(other: Scanner) : Boolean {
        val otherSet = other.lst.toSet()
        for (rotation in rotations) {
            for (dx in -1..1 step 2) {
                for (dy in -1..1 step 2) {
                    for (dz in -1..1 step 2) {
                        val points = lst.map {
                            Point(rotation.first(it) * dx, rotation.second(it) * dy, rotation.third(it) * dz)
                        }
                        for (p in points) {
                            for (op in other.lst) {
                                if (points.count { otherSet.contains(it - p + op) } >= 12) {
                                    lst.clear()
                                    lst.addAll(points.map { it - p + op })
                                    position = op - p
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }
}

fun main() {
    val data = readAllLines()
    val scanners = mutableListOf<Scanner>()
    for (str in data) {
        when {
            str.isEmpty() -> continue
            str.startsWith("---") -> scanners.add(Scanner(mutableListOf()))
            else -> scanners.last().lst.add(str.split(",").map { it.toInt() }.let { Point(it[0], it[1], it[2]) })
        }
    }
    rebuild(scanners)
    println(easy(scanners))
    println(hard(scanners))
}

fun rebuild(data: List<Scanner>) {
    val merged = BooleanArray(data.size) { it == 0 }
    data[0].position = Point(0,0,0)
    val q = mutableListOf(0)
    var idx = 0
    while (idx < q.size) {
        val x = q[idx++]
        for (i in data.indices) {
            if (!merged[i] && data[i].merge(data[x])) {
                merged[i] = true
                q.add(i)
            }
        }
    }
    require(q.size == data.size)
}

private fun easy(data: List<Scanner>) = data.flatMap { it.lst }.distinct().size
private fun hard(data: List<Scanner>) = data.maxOf { a -> data.maxOf { b ->
    (a.position - b.position).let { abs(it.x) + abs(it.y) + abs(it.z) }
} }
