package frameSharedVarLocalVar

fun main(args: Array<String>) {
    var var1 = 1
    foo {
        //Breakpoint!
        var1 = 2
    }
}

inline fun foo(f: () -> Unit) {
    f()
}

// PRINT_FRAME
// SHOW_KOTLIN_VARIABLES

// EXPRESSION: var1
// RESULT: 1: I