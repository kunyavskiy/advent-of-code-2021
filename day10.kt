fun main() {
    val data = readAllLines()
    println(easy(data))
    println(hard(data))
}

private val opens = "([{<"
private val closes = ")]}>"
private val scores = listOf(3, 57, 1197, 25137)

private fun score(s: String): Pair<List<Char>?, Int> = s.fold(mutableListOf('?')) { x, c ->
    x.apply {
        when (c) {
            in opens -> add(closes[opens.indexOf(c)])
            in closes -> removeAt(x.size - 1).takeIf { it == c } ?: return null to scores[closes.indexOf(c)]
        }
    }
}.let { it.reversed().dropLast(1) to 0 }

private fun easy(data: List<String>) = data.sumOf { score(it).second }

private fun hard(data: List<String>) = data.mapNotNull { score(it).first }.map {
    it.fold(0L) { acc, c -> acc * 5 + closes.indexOf(c).also { require(it != -1) } + 1 }
}.sorted().let { it[it.size / 2] }