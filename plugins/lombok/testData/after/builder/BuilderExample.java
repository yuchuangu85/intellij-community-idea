// Generated by delombok at Wed Oct 02 19:12:43 GMT 2019

public class BuilderExample {
  private String name;
  private int age;

  @java.lang.SuppressWarnings("all")
  BuilderExample(final String name, final int age) {
    this.name = name;
    this.age = age;
  }


  @java.lang.SuppressWarnings("all")
  public static class BuilderExampleBuilder {
    @java.lang.SuppressWarnings("all")
    private String name;
    @java.lang.SuppressWarnings("all")
    private int age;

    @java.lang.SuppressWarnings("all")
    BuilderExampleBuilder() {
    }

    @java.lang.SuppressWarnings("all")
    public BuilderExampleBuilder name(final String name) {
      this.name = name;
      return this;
    }

    @java.lang.SuppressWarnings("all")
    public BuilderExampleBuilder age(final int age) {
      this.age = age;
      return this;
    }

    @java.lang.SuppressWarnings("all")
    public BuilderExample build() {
      return new BuilderExample(name, age);
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
      return "BuilderExample.BuilderExampleBuilder(name=" + this.name + ", age=" + this.age + ")";
    }
  }

  @java.lang.SuppressWarnings("all")
  public static BuilderExampleBuilder builder() {
    return new BuilderExampleBuilder();
  }
}