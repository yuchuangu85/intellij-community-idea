// AFTER-WARNING: Variable 'foo1' is never used
// AFTER-WARNING: Variable 'foo2' is never used
import Outer.Middle.Inner.Companion.foo

class Outer {
    class Middle {
        class Inner {
            companion object {
                fun foo() {}
            }
        }
    }
}

class Test() {
    fun test2() {
        val foo2 = 42
        foo()
    }
    fun test() {
        val foo1 = 1
        foo<caret>()
    }
}