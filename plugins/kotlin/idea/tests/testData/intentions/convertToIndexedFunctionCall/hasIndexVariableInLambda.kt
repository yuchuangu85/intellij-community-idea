// INTENTION_TEXT: "Convert to 'forEachIndexed'"
// WITH_RUNTIME
// AFTER-WARNING: Parameter 'index1' is never used, could be renamed to _
// AFTER-WARNING: Variable 'index' is never used
fun test(list: List<String>) {
    list.<caret>forEach { s ->
        var index = 0
        println(s)
    }
}