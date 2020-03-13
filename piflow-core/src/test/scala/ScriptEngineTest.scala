/**
  * Created by bluejoe on 2018/5/6.
  */

import java.util
import javax.script.{Compilable, Invocable, ScriptEngineManager}

import jdk.nashorn.api.scripting.{ScriptObjectMirror, ScriptUtils}
import org.junit.{Assert, Test}

class ScriptEngineTest {
  @Test
  def testJs(): Unit = {
    val engine = new ScriptEngineManager().getEngineByName("javascript");
    engine.eval("function f(s){return s.toUpperCase();}");
    val s = engine.asInstanceOf[Invocable].invokeFunction("f", "bluejoe");
    Assert.assertEquals("BLUEJOE", s);
    Assert.assertEquals(classOf[String], s.getClass);

    val s2 = engine.eval("(function (){return java.util.Arrays.asList([1,2,3]);})();");
    println(s2);

    println(engine.eval("1;").getClass);
    println(engine.eval("'abc';").getClass);
    println(engine.eval("true;").getClass);
    println(engine.eval("1.1;").getClass);
    println(engine.eval("var x = {'a':1}; x;").getClass);
    println(engine.eval("new java.lang.Object()").getClass);
    println(engine.eval("java").getClass);
  }

  @Test
  def testJs2(): Unit = {
    val engine = new ScriptEngineManager().getEngineByName("javascript");
    val s = engine.eval("""["1","2","3"]""");
    println(s);
    val m = ScriptUtils.convert(s, classOf[Array[String]]);
    println(m);
  }

  @Test
  def testTs(): Unit = {
    val engine = new ScriptEngineManager().getEngineByName("typescript");
    val s = engine.eval("""["1","2","3"]""");
    println(s);
    val m = ScriptUtils.convert(s, classOf[Array[String]]);
    println(m);
  }

  /*@Test
  def testScala(): Unit = {
    val engine = new ScriptEngineManager().getEngineByName("scala");
    val settings = engine.asInstanceOf[scala.tools.nsc.interpreter.IMain].settings
    settings.usejavacp.value = true
    println(engine.eval("1+20"));
    val c = engine.asInstanceOf[Compilable].compile(
      """var value="hello";
        ((s:String)=>{
        s.toUpperCase();
        })(value);
      """);
    val b = engine.createBindings();
    b.put("value", "bluejoe");
    val s = c.eval(b);

    //val s = engine.asInstanceOf[Invocable].invokeFunction("f", "bluejoe");
    println(s);
  }*/
}
