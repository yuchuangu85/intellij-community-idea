// INTENTION_TEXT: "Import members from 'java.util.regex.Pattern'"
// WITH_RUNTIME
// AFTER-WARNING: Variable 'field' is never used
// AFTER-WARNING: Variable 'fieldFqn' is never used

import java.util.regex.Pattern
import java.util.regex.Pattern.matches

fun foo() {
    <caret>Pattern.matches("", "")

    val field = Pattern.CASE_INSENSITIVE

    Pattern.compile("")

    val fieldFqn = java.util.regex.Pattern.CASE_INSENSITIVE
}
