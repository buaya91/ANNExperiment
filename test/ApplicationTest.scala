import org.scalatestplus.play._
import controllers.Application
import orm.ExperimentResults
import play.api.test.{FakeHeaders, FakeRequest}
import play.api.test.Helpers._
import play.api.data._

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.{StaticQuery, GetResult}
import scala.slick.lifted.TableQuery

class ApplicationTest extends PlaySpec {
  "Application " should {
    "render index page as first page" in {
      val indexAction = Application.index
      val request = FakeRequest(GET, "/")
      val result = call(indexAction, request)

      status(result) mustEqual OK
      contentAsString(result) contains "Welcome to Play"
    }

    "show form when route to /exp/set" in {
      val setAction = Application.setExp
      val request = FakeRequest(GET, "/exp/set")
      val result = call(setAction, request)

      status(result) mustEqual OK
      contentAsString(result) contains "Data Size"
    }

    "print table when experiment finished running" in {
      val runAction = Application.runExp
      val data = "dataSize=2,3,4&clusterSize=4&noIteration=10"
      val request = FakeRequest(POST, "/exp/run", FakeHeaders(), body = data )
      val result = call(runAction, request)

      status(result) mustEqual OK
      contentAsString(result) contains "Data Size"
    }

    "save table into database" in {
      val saveAction = Application.saveResults
      val data = "DataSize[]=1" +
          "&ClusterSize[]=4" +
          "&NoIteration[]=10" +
          "&Algorithm[]=ANN" +
          "&TimeUsed[]=266"
      val request = FakeRequest(POST, "/exp/save", FakeHeaders(), body = data )
      val result = call(saveAction, request)

      val resultsTable = TableQuery[ExperimentResults]
      val db = Database.forURL("jdbc:h2:mem:experiment", driver = "org.h2.Driver")

      db.withDynSession { session =>
        val qr = StaticQuery.queryNA("SHOW TABLES")
      }
    }
  }
}
