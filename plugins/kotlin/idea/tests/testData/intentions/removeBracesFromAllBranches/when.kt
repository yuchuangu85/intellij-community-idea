// AFTER-WARNING: Parameter 'i' is never used
fun test(i: Int) {
    when<caret> (i) {
        1 -> {
            println(1)
        }
        2 -> {
            println(2)
        }
        else -> {
            println(3)
        }
    }
}

fun println(i: Int) {}