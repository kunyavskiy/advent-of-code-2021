
abstract class Packet {
    abstract val version: Int
    abstract val type: Int
    abstract fun versionSum(): Int
    abstract fun eval(): Long
}

data class LiteralPacket(override val version: Int, override val type: Int, val value:Long) : Packet() {
    override fun versionSum() = version
    override fun eval(): Long = value
}

data class OperatorPacket(override val version: Int, override val type: Int, val value:List<Packet>) : Packet() {
    override fun versionSum() = version + value.sumOf { it.versionSum() }
    override fun eval() = when (type) {
        0 -> value.sumOf { it.eval() }
        1 -> value.map { it.eval() }.reduce(Long::times)
        2 -> value.minOf { it.eval() }
        3 -> value.maxOf { it.eval() }
        5 -> if (value[0].eval() > value[1].eval()) 1L else 0L
        6 -> if (value[0].eval() < value[1].eval()) 1L else 0L
        7 -> if (value[0].eval() == value[1].eval()) 1L else 0L
        else -> TODO()
    }
}

fun main() {
    val data = readAllLines().single()
        .map { it.toString().toLong(radix = 16).toString(2).padStart(4, '0') }
        .joinToString("")
    println(easy(data))
    println(hard(data))
}

class Stream(val data: String) {
    private var pos = 0
    val remaining get() = data.length - pos
    fun nextIntBits(cnt: Int) = data.substring(pos, pos + cnt).toInt(2).also {
        pos += cnt
    }

    fun subStream(cnt: Int) = Stream(data.substring(pos, pos + cnt)).also {
        pos += cnt
    }
}

private fun parse(data: Stream) : Packet {
    val version = data.nextIntBits(3)
    val type = data.nextIntBits(3)
    return when (type) {
        4 -> buildString {
            while (true) {
                val x = data.nextIntBits(5)
                append((x and 15).toString(16))
                if ((x and 16) == 0) break
            }
        }.let { LiteralPacket(version, type, it.toLong(16)) }
        else -> when (data.nextIntBits(1)) {
            0 -> @OptIn(ExperimentalStdlibApi::class) buildList {
                data.subStream(data.nextIntBits(15)).apply {
                    while (remaining > 3) {
                        add(parse(this))
                    }
                }
            }
            1 -> List(data.nextIntBits(11)) { parse(data) }
            else -> TODO()
        }.let { OperatorPacket(version, type, it) }
    }
}

private fun easy(data: String) = parse(Stream(data)).versionSum()

private fun hard(data: String) = parse(Stream(data)).eval()