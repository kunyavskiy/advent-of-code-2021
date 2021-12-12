fun main() {
    val data = readAllLines()
    println(easy(data))
    println(hard(data))
}

private fun List<String>.common(index:Int) = if (count { it[index] == '0' } > count { it[index] == '1' }) '0' else '1'
private fun List<String>.uncommon(index:Int) = if (count { it[index] == '0' } <= count { it[index] == '1' }) '0' else '1'

private fun easy(data: List<String>) = data[0].indices
    .map(data::common)
    .joinToString("")
    .toInt(radix = 2).let {
        it * ((1 shl data[0].length) - it - 1)
    }

private fun filter(data: List<String>, common: List<String>.(Int) -> Char) =
    data.indices.fold(data) { acc, index ->
        acc.takeIf { it.size == 1 } ?: acc.filter { it[index] == acc.common(index) }
    }.single().toInt(radix = 2)

private fun hard(data: List<String>) = filter(data) { common(it) } * filter(data) { uncommon(it) }
