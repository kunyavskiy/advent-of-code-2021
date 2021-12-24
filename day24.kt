import kotlin.math.*

private abstract class Expr {
    abstract fun simplifyCustom() : Expr
    abstract val possibleValues: List<Long>?
    abstract val minValue: Long
    abstract val maxValue: Long
    fun simplify(): Expr = if (possibleValues?.size == 1) ConstExpr(possibleValues!!.single()) else
        simplifyCustom().let { if (it != this) it.simplify() else it }
    operator fun plus(other: Expr) = AddExpr(this, other).simplify()
    operator fun times(other: Expr) = MulExpr(this, other).simplify()
    operator fun div(other: Expr) = DivExpr(this, other).simplify()
    operator fun rem(other: Expr) = ModExpr(this, other).simplify()
}
private data class ConstExpr(val x: Long) : Expr() {
    override val possibleValues = listOf(x)
    override val minValue = x
    override val maxValue = x
    override fun simplifyCustom() = this
    override fun toString() = x.toString()
}

private data class VarExpr(val x: Int) : Expr() {
    override val possibleValues = (1L..9L).toList()
    override val minValue = 1L
    override val maxValue = 9L
    override fun simplifyCustom() = this
    override fun toString() = "x$x"
}
private data class AddExpr(val l:Expr, val r:Expr) : Expr() {
    override val possibleValues =
        if (l.possibleValues == null || r.possibleValues == null)
            null
        else
            l.possibleValues!!.flatMap { lit -> r.possibleValues!!.map { lit + it } }.distinct().takeIf {
                it.size <= 1000
            }?.distinct()
    override val minValue = l.minValue + r.minValue
    override val maxValue = l.maxValue + r.maxValue

    override fun simplifyCustom() : Expr = when {
        l is ConstExpr && r is ConstExpr -> ConstExpr(l.x + r.x)
        l is AddExpr && l.r is ConstExpr && r is ConstExpr -> l.l + ConstExpr(l.r.x + r.x)
        l is ConstExpr -> AddExpr(r, l).simplify()
        r is ConstExpr && r.x == 0L -> l
        l is AddExpr && l.r is ConstExpr -> l.l + (l.r + r)
        r is AddExpr && r.r is ConstExpr -> (l + r.l) + r.r
        l is IfEqExpr -> IfEqExpr(l.l, l.r, l.onEq + r, l.onNeq + r)
        r is IfEqExpr -> IfEqExpr(r.l, r.r, l + r.onEq, l + r.onNeq)
        else -> this
    }

    override fun toString() = "($l+$r)"
}
private data class MulExpr(val l:Expr, val r:Expr) : Expr() {
    override fun toString() = "($l*$r)"
    override val possibleValues = if (l.possibleValues == null || r.possibleValues == null)
            null
        else
            l.possibleValues!!.flatMap { lit -> r.possibleValues!!.map { lit * it } }.distinct().takeIf {
                it.size <= 1000
            }?.distinct()
    override val minValue = minOf(l.minValue * r.minValue, l.minValue * r.maxValue, l.maxValue * r.minValue, l.maxValue * r.maxValue)
    override val maxValue = minOf(l.minValue * r.minValue, l.minValue * r.maxValue, l.maxValue * r.minValue, l.maxValue * r.maxValue)
    override fun simplifyCustom() : Expr = when {
        l is ConstExpr && r is ConstExpr -> ConstExpr(l.x * r.x)
        l is MulExpr && l.r is ConstExpr && r is ConstExpr -> l.l * ConstExpr(l.r.x * r.x)
        l is ConstExpr -> r * l
        r is ConstExpr && r.x == 0L -> ConstExpr(0)
        r is ConstExpr && r.x == 1L -> l
        r is ConstExpr && l is AddExpr -> (l.l * r) + (l.r * r)
        r is ConstExpr && l is DivExpr -> (l.l * r) / l.r
        l is IfEqExpr -> IfEqExpr(l.l, l.r, l.onEq * r, l.onNeq * r)
        r is IfEqExpr -> IfEqExpr(r.l, r.r, l * r.onEq, l * r.onNeq)
        else -> this
    }
}
private data class DivExpr(val l:Expr, val r:Expr) : Expr() {
    override fun toString() = "($l/$r)"
    override val possibleValues = if (l.possibleValues == null || r.possibleValues == null)
            null
        else
            l.possibleValues!!.flatMap { lit -> r.possibleValues!!.filter { it > 0 }.map { lit / it } }.distinct().takeIf {
                it.size <= 1000
            }
    override val minValue = l.minValue / r.maxValue
    override val maxValue = l.maxValue / r.minValue
    override fun simplifyCustom() : Expr = when {
        l is ConstExpr && r is ConstExpr -> ConstExpr(l.x / r.x)
        l is DivExpr -> l.l / (l.r * r)
        r is ConstExpr && r.x == 1L -> l
        l is MulExpr && r is ConstExpr && l.r is ConstExpr && l.r.x % r.x == 0L -> l.l * (l.r / r)
        l is AddExpr && r is ConstExpr ->
                listOf(this, ((l.l / r) + (l.r / r))).minByOrNull {
                    it.toString().length
                }!!
        l is IfEqExpr -> IfEqExpr(l.l, l.r, l.onEq / r, l.onNeq / r)
        else -> this
    }
}
private data class ModExpr(val l:Expr, val r:Expr) : Expr() {
    override fun toString() = "($l%$r)"
    override val minValue = 0L
    override val maxValue = r.maxValue - 1
    override val possibleValues = if (l.possibleValues == null || r.possibleValues == null)
            null
        else
            l.possibleValues!!.flatMap { lit -> r.possibleValues!!.filter { it > 0 }.map { lit % it } }.distinct().takeIf {
                it.size <= 1000
            }
    override fun simplifyCustom() : Expr = when {
        l is ConstExpr && r is ConstExpr -> ConstExpr(l.x % r.x)
        r is ConstExpr && l.possibleValues != null && l.possibleValues!!.all { it < r.x } -> l
        l is AddExpr -> listOf(this, ModExpr((l.l % r) + (l.r % r), r)).minByOrNull { it.toString().length }!!
        l is MulExpr -> listOf(this, ModExpr((l.l % r) * (l.r % r), r)).minByOrNull { it.toString().length }!!
        l is IfEqExpr -> IfEqExpr(l.l, l.r, l.onEq % r, l.onNeq % r)
        else -> this
    }
}
private data class IfEqExpr(val l:Expr, val r:Expr, val onEq: Expr, val onNeq: Expr) : Expr() {
    override fun toString() = "(if ($l==$r) $onEq else $onNeq)"
    override val possibleValues =
        if (onEq.possibleValues == null || onNeq.possibleValues == null)
            null
        else
            (onEq.possibleValues!! + onNeq.possibleValues!!).distinct().takeIf {
                it.size <= 1000
            }
    override val minValue = min(onEq.minValue, onNeq.minValue)
    override val maxValue = max(onEq.maxValue, onNeq.maxValue)

    override fun simplifyCustom() = when {
        onEq.toString() == onNeq.toString() -> onEq
        l is IfEqExpr && r is ConstExpr && r.x == 1L -> l
        l is IfEqExpr && r is ConstExpr && r.x == 0L -> IfEqExpr(l.l, l.r, l.onNeq, l.onEq).simplify()
        onEq is IfEqExpr && onEq.l.toString() == l.toString() && onEq.r.toString() == r.toString() -> IfEqExpr(
            l, r, onEq.onEq, onNeq
        ).simplify()
        onNeq is IfEqExpr && onNeq.l.toString() == l.toString() && onNeq.r.toString() == r.toString() -> IfEqExpr(
            l, r, onEq, onNeq.onNeq
        ).simplify()
        r.possibleValues == null -> this
        r.possibleValues!!.none { it in l.minValue..l.maxValue } -> onNeq
        l.possibleValues == null -> this
        l.possibleValues!!.intersect(r.possibleValues!!.toSet()).isEmpty() -> onNeq
        l.possibleValues!!.size == 1 && r.possibleValues!!.size == 1 ->
            if (l.possibleValues!!.single() == r.possibleValues!!.single()) onEq else onNeq
        else -> this
    }
}

private fun simplifyNestedIf(expr: Expr, trueConds:Set<String>, falseConds:Set<String>, replaceLeafs: Boolean) : Expr = if (expr !is IfEqExpr) {
    if (replaceLeafs) {
        IfEqExpr(expr, ConstExpr(0), ConstExpr(1), ConstExpr(0)).simplify()
    } else {
        expr
    }
} else {
    val c = "${expr.l}==${expr.r}"
    if (c in trueConds)
        simplifyNestedIf(expr.onEq, trueConds, falseConds, replaceLeafs)
    else if (c in falseConds)
        simplifyNestedIf(expr.onNeq, trueConds, falseConds, replaceLeafs)
    else if (expr.l is IfEqExpr) {
        simplifyNestedIf(IfEqExpr(
            expr.l.l,
            expr.l.r,
            IfEqExpr(expr.l.onEq, expr.r, expr.onEq, expr.onNeq),
            IfEqExpr(expr.l.onNeq, expr.r, expr.onEq, expr.onNeq),
        ), trueConds, falseConds, replaceLeafs)
    } else
        IfEqExpr(
            simplifyNestedIf(expr.l, trueConds, falseConds, false),
            simplifyNestedIf(expr.r, trueConds, falseConds, false),
            simplifyNestedIf(expr.onEq, trueConds + c, falseConds, replaceLeafs),
            simplifyNestedIf(expr.onNeq, trueConds, falseConds + c, replaceLeafs)).simplify()
}

private fun nameToReg(s: String) = when (s) {
    "w" -> 0
    "x" -> 1
    "y" -> 2
    "z" -> 3
    else -> TODO(s)
}

private fun nameToVal(acc:List<Expr>, s: String) = s.toIntOrNull()?.let { ConstExpr(it.toLong()) } ?:
    acc[nameToReg(s)]


private fun Expr.collectOnes() : List<List<String>> = when (this) {
    is ConstExpr -> if (x == 1L) listOf(emptyList()) else emptyList()
    is IfEqExpr -> onEq.collectOnes().map { it + "$l==$r" } + onNeq.collectOnes().map { it + "$l!=$r" }
    else -> TODO()
}

fun main() {
    var varId = 1
    val data = readAllLines().map { it.split(" ") }

    val r = data.fold(
        MutableList<Expr>(4) { ConstExpr(0) }
    ) { acc, v ->
        when (v[0]) {
            "inp" -> acc[nameToReg(v[1])] = VarExpr(varId++).simplify()
            "add" -> acc[nameToReg(v[1])] = nameToVal(acc, v[1]) + nameToVal(acc, v[2])
            "mul" -> acc[nameToReg(v[1])] = nameToVal(acc, v[1]) * nameToVal(acc, v[2])
            "div" -> acc[nameToReg(v[1])] = nameToVal(acc, v[1]) / nameToVal(acc, v[2])
            "mod" -> acc[nameToReg(v[1])] = nameToVal(acc, v[1]) % nameToVal(acc, v[2])
            "eql" -> acc[nameToReg(v[1])] = IfEqExpr(nameToVal(acc, v[1]), nameToVal(acc, v[2]), ConstExpr(1L), ConstExpr(0L)).simplify()
            else -> TODO(v[0])
        }
        acc
    }[3].let {
        simplifyNestedIf(it, emptySet(), emptySet(), true)
    }
    println("$r")
    println(r.collectOnes().joinToString("\n"))
}

