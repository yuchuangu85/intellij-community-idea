// "Cast 1st argument to 'long'" "true"
class a {
    void f(Long l, String... s) {}
    void g() {
        f(<caret>0, "");
    }
}
