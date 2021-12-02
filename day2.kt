private enum class OP {
    FORWARD,
    UP,
    DOWN
}

private data class Instruction(val op: OP, val arg: Int)

fun main() {
    val data = readAllLines()
        .map { it.split(" ") }
        .map { Instruction(OP.valueOf(it[0].uppercase()), it[1].toInt()) }
    println(easy(data))
    println(hard(data))
}

private data class EasyPosition(val h: Long, val v: Long)

private fun easy(data: List<Instruction>) = data.fold(EasyPosition(0, 0)) { acc, (op, arg) ->
    when (op) {
        OP.FORWARD -> acc.copy(h = acc.h + arg)
        OP.UP -> acc.copy(v = acc.v - arg)
        OP.DOWN -> acc.copy(v = acc.v + arg)
    }
}.let { it.h * it.v }

private data class HardPosition(val h: Long, val v: Long, val a:Long)

private fun hard(data: List<Instruction>) =  data.fold(HardPosition(0, 0, 0)) { acc, (op, arg) ->
    when (op) {
        OP.FORWARD -> acc.copy(h = acc.h + arg, v = acc.v + arg * acc.a,)
        OP.UP -> acc.copy(a = acc.a - arg)
        OP.DOWN -> acc.copy(a = acc.a + arg)
    }
}.let { it.h * it.v }
