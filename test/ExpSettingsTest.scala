import org.scalatest.{MustMatchers, FlatSpec}
import models.{ExpSettings, ExpSettingPerRun, ExpVariable}
import models.VariableType._

/**
 * Created by qingwei on 3/16/15.
 */
class ExpSettingsTest extends FlatSpec with MustMatchers{
  "Experiment setting" should "be instantiate by 3 strings" in {
    val db = "100"
    val cls = "10"
    val noit = "2, 10"
    val expSetting1 = ExpSettings(db, cls, noit)

    val var1 = ExpVariable(Fixed, "DataSize", 100)
    val var2 = ExpVariable(Fixed, "ClusterSize", 10)
    val var3 = ExpVariable(Independent, "NoIteration", 2,10)

    assert(expSetting1.fixedVars == Set(var1, var2))
    assert(expSetting1.independentVar == var3)
  }

  it should "throw exception when there is more than one Independent variable" in {
    val var1 = "10"
    val var2 = "10,1"
    val var3 = "10,2"
    val exception1 = intercept[IllegalArgumentException] {
      ExpSettings(var1, var2, var3)
    }
    assert(exception1.getMessage contains "should have one and only one Independent variable")
  }

  it should "throw exception when there is no Independent variable" in {
    val exception1 = intercept[IllegalArgumentException] {
      ExpSettings("10", "10", "10")
    }
    assert(exception1.getMessage contains "should have one and only one Independent variable")
  }

  ignore should "throw exception when there are variables with duplicate name" in {
    val var1 = ExpVariable(Fixed, "v1", 10)
    val var2 = ExpVariable(Fixed, "v1", 20)
    val var3 = ExpVariable(Independent, "v3", 10, 3)
    val exception1 = intercept[IllegalArgumentException] {

    }
    assert(exception1.getMessage contains "variables should not have same name")
  }

  it should "return a set of ExperimentSettingsPerRun when variableSetsPerRun is invoked" in {
    val var1 = ExpVariable(Fixed, "DataSize", 10)
    val var2 = ExpVariable(Fixed, "ClusterSize", 10)
    val var3 = ExpVariable(Independent, "NoIteration", 10, 1)
    val expSetting1 = ExpSettings("10", "10", "10,1")

    val settingsSet: Set[ExpSettingPerRun] = expSetting1.variableSetsPerRun

    info("no of variables in Set returned should be the same as the no of values of Independent variable")
    assert(settingsSet.size == 2)

    info("set's element inherit all Fixed variable and one value of Independent variable in disjoint fashion")
    val setting1 = ExpSettingPerRun(Set(var1, var2, ExpVariable(Fixed, "NoIteration", 10)))
    val setting2 = ExpSettingPerRun(Set(var1, var2, ExpVariable(Fixed, "NoIteration", 1)))
    assert(settingsSet == Set(setting1, setting2))
  }
}
