// WITH_RUNTIME
// AFTER-WARNING: Variable 'foo' is never used
fun test(i: UInt) {
    val foo = i.toUInt()<caret>
}