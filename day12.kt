fun main() {
    val data = readAllLines().map { it.split("-") }.flatMap { listOf(it, it.reversed()) }.groupBy({ it[0] }, { it[1] })
    println(easy(data))
    println(hard(data))
}

private fun go(g: Map<String, List<String>>, path: List<String>, dupAllowed: Boolean): Int =
    if (path.last() == "end") 1 else g[path.last()]!!.sumOf {
        go(
            g, path + listOf(it), when {
                it[0] in 'A'..'Z' -> dupAllowed
                it in path && dupAllowed && it != "start" -> false
                it !in path -> dupAllowed
                else -> return@sumOf 0
            }
        )
    }

private fun easy(data: Map<String, List<String>>) = go(data, listOf("start"), false)
private fun hard(data: Map<String, List<String>>) = go(data, listOf("start"), true)