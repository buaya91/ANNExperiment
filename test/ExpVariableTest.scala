import models.ExpVariable
import models.VariableType._
import org.scalatest.{FlatSpec, MustMatchers}

/**
 * Created by qingwei on 3/16/15.
 */
class ExpVariableTest extends FlatSpec with MustMatchers{
  "A fixed variable" should "throw exception when it has more than one value" in {
    val exception = intercept[IllegalArgumentException]{
      val fixedVariable = ExpVariable(Fixed, "TestVariable1", 2,2)
    }
    assert(exception.getMessage contains "fixed variable cannot have more than one variable")
  }
  it should "able to be instantiate with Type, Name and one Value" in {
    val fixedVariable = ExpVariable(Fixed, "TestVariable1", 2)
    assert(fixedVariable.value(0) == 2)
    assert(fixedVariable.vName == "TestVariable1")
    assert(fixedVariable.vType == Fixed)
  }

  "A independent variable" should "throw exception when created with one or less value" in {
    val exception = intercept[IllegalArgumentException]{
      val independentVariable = ExpVariable(Independent, "TestVariable1", 2)
    }
    assert(exception.getMessage contains "independent variable must have more than one value")
  }
  it should "able to be instantiate with Type, Name and more than one value" in {
    val independentVariable = ExpVariable(Independent, "TestVariable1", 2, 2, 3)
    assert(independentVariable.vType == Independent)
    assert(independentVariable.vName == "TestVariable1")
    assert(independentVariable.value == Seq(2, 2, 3))
  }
}
