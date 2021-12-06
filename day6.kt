import java.math.*

fun main() {
    val data = readAllLines().single().let { it.splitDigits().map { it.toInt() } }
    println(easy(data))
    println(hard(data))
}

private val cache = mutableMapOf<Int, BigInteger>()
private fun dp(days:Int) : BigInteger = cache.getOrPut(days) {
    when {
        days <= 0 -> BigInteger.ONE
        else -> dp(days - 7) + dp(days - 9)
    }
}

private fun get(data: List<Int>, days: Int) = data
    .indices
    .groupBy { data[it] }
    .mapValues { it.value.size }
    .entries
    .map { BigInteger.valueOf(it.value.toLong()) * dp(days - it.key) }
    .reduce(BigInteger::plus)


private fun easy(data: List<Int>) = get(data, 80)
private fun hard(data: List<Int>) = get(data, 256)
