// WITH_RUNTIME
// AFTER-WARNING: Parameter 'f' is never used
fun foo(f: () -> Unit) {}

class Bar {
    fun bar() {}
}

class Test {
    fun test() {
        Bar().run {
            foo { <caret>bar() }
        }
    }
}