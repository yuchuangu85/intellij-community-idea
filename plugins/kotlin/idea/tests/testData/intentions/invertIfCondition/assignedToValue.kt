// AFTER-WARNING: Variable 't' is never used
fun main() {
    val a = -1
    val t = <caret>if (a > 0) a /* a */ else -a /* -a */
}