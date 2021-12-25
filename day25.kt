fun main() {
    val data = readAllLines()
    println(easy(data))
}

fun List<String>.move(right: Boolean) : List<String> {
    val x = Array(size) { CharArray(this[0].length) { '.' } }
    for (i in x.indices) {
        for (j in x[0].indices) {
            if (this[i][j] == '>') {
                if (right && this[i][(j + 1) % x[0].size] == '.') {
                    x[i][(j + 1) % x[0].size] = '>'
                } else {
                    x[i][j] = '>'
                }
            } else if (this[i][j] == 'v') {
                if (!right && this[(i + 1) % x.size][j] == '.') {
                    x[(i + 1) % x.size][j] = 'v'
                } else {
                    x[i][j] = 'v'
                }
            }
        }
    }
    return x.map { it.concatToString() }
}




private fun easy(data: List<String>) = generateSequence(data) { acc ->
    println(acc.joinToString("\n"))
    println()
    println()
    acc.move(true).move(false).takeIf { it.toString() != acc.toString() }
}.count()
