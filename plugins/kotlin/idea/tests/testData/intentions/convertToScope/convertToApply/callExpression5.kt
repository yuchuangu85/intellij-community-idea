// WITH_RUNTIME
// AFTER-WARNING: Name shadowed: f
// AFTER-WARNING: Parameter 'f' is never used
// AFTER-WARNING: Parameter 'f' is never used
// AFTER-WARNING: Parameter 'i' is never used
// AFTER-WARNING: Parameter 'i' is never used
// AFTER-WARNING: Variable 'f' is never used
class Foo {
    fun foo(i: Int) {}

    fun test(f: Foo) {
        val f = Foo()<caret>
        f.foo(1)
        f.foo(2)
        bar(2, this)
    }
}

fun bar(i: Int, f: Foo) {}

