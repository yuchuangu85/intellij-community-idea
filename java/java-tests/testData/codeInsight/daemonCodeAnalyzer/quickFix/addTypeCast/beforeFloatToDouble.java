// "Cast argument to 'double'" "true"
class a {
 void test(Double d) {}
 
 void foo() {
   test(<caret>1.0f);
 }
}

