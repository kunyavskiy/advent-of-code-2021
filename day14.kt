private val Char.letterCode get() = code - 'A'.code

private data class Rule(val a: Int, val b: Int, val to: Int)

fun main() {
    val data = readAllLines()
    val dataStart = data[0].map { it.letterCode }
    val dataRules = data.drop(2).map { it.split(" -> ") }.map {
        Rule(it[0][0].letterCode, it[0][1].letterCode, it[1][0].letterCode)
    }
    println(easy(dataStart, dataRules))
    println(hard(dataStart, dataRules))
}

private fun <T> List<Pair<T, Long>>.mergeSame() = groupBy { it.first }
    .mapValues { (_, v) -> v.sumOf { it.second } }

private fun process(rules: List<Rule>, a: Int, b: Int) =
    rules.singleOrNull { it.a == a && it.b == b }?.let {
        listOf(a to it.to, it.to to b)
    } ?: listOf(a to b)

private fun solve(start: List<Int>, rules: List<Rule>, count: Int) =
    generateSequence((listOf(-1) + start).zipWithNext().map { it to 1L }.mergeSame()) { counts ->
        counts.flatMap { (key, value) ->
            process(rules, key.first, key.second).map { it to value }
        }.mergeSame()
    }.take(count + 1)
        .last()
        .map { (k, v) -> k.second to v }
        .mergeSame().values
        .sorted()
        .let { it.last() - it[0] }

private fun easy(start: List<Int>, rules: List<Rule>) = solve(start, rules, 10)
private fun hard(start: List<Int>, rules: List<Rule>) = solve(start, rules, 40)
