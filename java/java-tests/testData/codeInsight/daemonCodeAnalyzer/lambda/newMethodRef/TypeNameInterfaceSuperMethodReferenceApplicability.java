interface I {
  default void a() {}
}

interface J extends I {}

class A implements I {}
class B extends A {}
class C implements J {}

class Test1 implements I { { Runnable r = I.super::a; } }
class Test2 implements J { { Runnable r = <error descr="'I' is not an enclosing class">I.super</error>::a; } }
class Test3 implements I, J { { Runnable r = <error descr="Bad type qualifier in default super call: redundant interface I is extended by J">I</error>.super::a; } }
class Test5 extends A implements I { { Runnable r = <error descr="Bad type qualifier in default super call: redundant interface I is extended by A">I</error>.super::a; } }
class Test6 extends A implements J { { Runnable r = <error descr="'I' is not an enclosing class">I.super</error>::a; } }
class Test7 extends B { { Runnable r = <error descr="'I' is not an enclosing class">I.super</error>::a; } }
class Test8 extends C { { Runnable r = <error descr="'I' is not an enclosing class">I.super</error>::a; } }

class Test {
  {
    class LocalJ implements I {
    }
    class Test3 extends LocalJ implements I {
      {
        Runnable r = <error descr="Bad type qualifier in default super call: redundant interface I is extended by LocalJ">I</error>.super::a;
      }
    }

    new I() {
      {
        Runnable r = I.super::a;
      }
    };

    new A() {
      {
        Runnable r = <error descr="'I' is not an enclosing class">I.super</error>::a;
      }
    };
  }

  interface D extends J {
    default void m() {
      <error descr="No enclosing instance of type 'Test.D' is in scope">D</error>.super.toString();
      J.super.toString();
    }
  }

  class E implements I {
    public void a() {}
  }
  
  class F extends E implements I {
    void bar() {
      <error descr="Bad type qualifier in default super call: redundant interface I is extended by Test.E">I</error>.super.a();
      Runnable r = <error descr="Bad type qualifier in default super call: redundant interface I is extended by Test.E">I</error>.super::a;
    }
  }
  
  class G extends A implements I {
    void bar() {
      <error descr="Bad type qualifier in default super call: redundant interface I is extended by A">I</error>.super.a();
      Runnable r = <error descr="Bad type qualifier in default super call: redundant interface I is extended by A">I</error>.super::a;
    }
  }
}

class InsideThisRxpression {
  interface Foo {
    void fooMethod();

    default Bar toBar() {
      return new Bar() {
        @Override
        public void fooMethod() {
          Foo.this.fooMethod();
        }
      };
    }
  }
  public interface Bar extends Foo {}
}

class SameDefaultMethodDifferentInheritors {
  interface A { default void a() {} }
  interface B extends A { default void a() {} }
  interface B1 extends A { }
  interface C extends A {}

  class Clazz implements B, C {
    {
      <error descr="Bad type qualifier in default super call: method a is overridden in SameDefaultMethodDifferentInheritors.B">C</error>.super.a();
    }
  }
  
  class Clazz1 implements B1, C {
    {
      C.super.a();
    }
  }
  
  class Clazz2 implements C {
    {
      C.super.a();
    }
  }
}
