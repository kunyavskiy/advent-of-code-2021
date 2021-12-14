import kotlin.math.*

fun main() {
    val data = readAllLines().single().let { it.splitDigits().map { it.toInt() } }
    println(easy(data))
    println(hard(data))
}


private fun easy(data: List<Int>) = (data.minOrNull()!!..data.maxOrNull()!!).minOf { x ->
    data.sumOf { (it - x).absoluteValue }
}

private fun hard(data: List<Int>) = (data.minOrNull()!!..data.maxOrNull()!!).minOf { x ->
    data.sumOf { (it - x).absoluteValue.let { it * (it + 1) / 2 } }
}
