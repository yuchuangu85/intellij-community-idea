// AFTER-WARNING: Parameter 'i' is never used
fun foo() {
    <caret>if (true) {
        bar(1)
    } else bar(2)
}

fun bar(i: Int) {}