fun main() {
    val data = readAllLines().map { it.toInt() }
    println(easy(data))
    println(hard(data))
}

private fun easy(data: List<Int>) = data.zipWithNext().count { it.second > it.first }

private fun hard(data: List<Int>) = easy(data.windowed(3).map { it.sum() })
