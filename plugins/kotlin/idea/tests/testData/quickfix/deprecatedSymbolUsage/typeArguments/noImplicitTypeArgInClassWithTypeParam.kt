// "Replace with 'this.minByOrNull(selector)'" "true"
// WITH_RUNTIME
class C<T> {
    fun test() {
        listOf(1).<caret>minBy { it }
    }
}