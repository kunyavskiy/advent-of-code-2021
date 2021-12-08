fun main() {
    val data = readAllLines().map { it.splitNonEmpty(" ").filterNot { it == "|" } }
    println(easy(data))
    println(hard(data))
}

private fun easy(data: List<List<String>>) = data.sumOf { it.takeLast(4).count { it.length in listOf(2, 3, 4, 7) } }

val D = listOf(
    "abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg"
)

fun genPermutations(length:Int) : Sequence<List<Int>> = when (length) {
    1 -> sequenceOf(listOf(0))
    else -> genPermutations(length - 1).flatMap { old ->
        (0 until length).asSequence().map { old.take(it) + listOf(length - 1) + old.drop(it) }
    }
}

fun genAll() = genPermutations(7).map { s ->
    s.indices.associate { (it + 'a'.code).toChar() to (s[it] + 'a'.code).toChar() }
}

fun String.remap(code: Map<Char, Char>) = map { code[it]!! }.sortedBy { it.code }.joinToString("")

fun decode(vals: List<String>) = genAll().single { code ->
    vals.all { it.remap(code) in D  }
}

private fun hard(data: List<List<String>>) : Int =
    data.sumOf { line ->
        decode(line.take(10)).let { code ->
            line.takeLast(4).joinToString("") {
                D.indexOf(it.remap(code)).toString()
            }.toInt()
        }
    }