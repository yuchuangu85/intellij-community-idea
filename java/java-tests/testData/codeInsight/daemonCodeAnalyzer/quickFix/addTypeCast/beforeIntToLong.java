// "Cast argument to 'long'" "true"
class a {
 void test(Long l) {}
 
 void foo() {
   test(<caret>123);
 }
}
