// AFTER-WARNING: Parameter 'a' is never used
fun <T> doSomething(a: T) {}

fun test(n: Int): String {
    <caret>return when(n) {
    1 -> {
        doSomething("***")
        "one"
    }
    else -> {
        doSomething("***")
        "two"
    }
}
