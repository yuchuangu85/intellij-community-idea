// AFTER-WARNING: Parameter 'f' is never used
fun foo(f: (Int) -> String) {}

fun test() {
    foo {<caret>
        if (it == 1) {
            return@foo "1"
        }
        "$it"
    }
}