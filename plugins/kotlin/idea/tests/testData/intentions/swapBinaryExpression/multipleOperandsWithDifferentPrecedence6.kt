// AFTER-WARNING: Variable 'rabbit' is never used
fun main() {
    val rabbit = 4 + 2 == 12 ||<caret> true && 5 + 5 == 10
}