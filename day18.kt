data class ExplodeResult(val tree: SnailFish, val addToLeft: Int, val addtoRight: Int)

sealed class SnailFish {
    abstract fun magnitude(): Int
    abstract fun split(): SnailFish?
    abstract fun explode(depth: Int): ExplodeResult?
    abstract fun addToFirst(add: Int): SnailFish
    abstract fun addToLast(add: Int): SnailFish
}

data class Leaf(val x: Int) : SnailFish() {
    override fun toString() = x.toString()
    override fun magnitude() = x
    override fun split() = Inner(Leaf(x / 2), Leaf(x - x / 2)).takeIf { x >= 10 }
    override fun explode(depth: Int) = null
    override fun addToFirst(add: Int) = Leaf(x + add)
    override fun addToLast(add: Int) = Leaf(x + add)
}

data class Inner(val left: SnailFish, val right: SnailFish) : SnailFish() {
    override fun toString() = "[$left, $right]"
    override fun magnitude() = 3 * left.magnitude() + 2 * right.magnitude()
    override fun split() = left.split()?.let { Inner(it, right) } ?: right.split()?.let { Inner(left, it) }
    override fun explode(depth: Int): ExplodeResult? = if (left is Leaf && right is Leaf) {
        ExplodeResult(Leaf(0), left.x, right.x).takeIf { depth >= 4 }
    } else {
        left.explode(depth + 1)?.let {
            ExplodeResult(Inner(it.tree, right.addToFirst(it.addtoRight)), it.addToLeft, 0)
        } ?: right.explode(depth + 1)?.let {
            ExplodeResult(Inner(left.addToLast(it.addToLeft), it.tree), 0, it.addtoRight)
        }
    }

    override fun addToFirst(add: Int) = Inner(left.addToFirst(add), right)
    override fun addToLast(add: Int) = Inner(left, right.addToLast(add))
}

private fun parseSnailFish(s: String): Pair<SnailFish, String> {
    if (s[0].isDigit()) {
        val prefix = s.takeWhile { it.isDigit() }
        return Leaf(prefix.toInt()) to s.substring(prefix.length)
    } else {
        require(s.startsWith("["))
        val (left, remaining) = parseSnailFish(s.substring(1))
        require(remaining.startsWith(","))
        val (right, remaning2) = parseSnailFish(remaining.substring(1))
        require(remaning2.startsWith("]"))
        return Inner(left, right) to remaning2.substring(1)
    }
}

fun plus(a: SnailFish, b: SnailFish) =
    generateSequence<SnailFish>(Inner(a, b)) {
        it.explode(0)?.tree ?: it.split()
    }.last()


fun main() {
    val data = readAllLines().map { parseSnailFish(it).first }
    println(easy(data))
    println(hard(data))
}

private fun easy(data: List<SnailFish>) = data.reduce(::plus).magnitude()
private fun hard(data: List<SnailFish>) = data.maxOf { x ->
    data.maxOf { y ->
        if (x != y) plus(x, y).magnitude() else Int.MIN_VALUE
    }
}
