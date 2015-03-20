package controllers

import models.{Experiment, ExpResults, ExpSettings}
import models.ExpStatus._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

object Application extends Controller {

  val expSettingForm = Form(
    mapping(
      "dataSize" -> nonEmptyText,
      "clusterSize" -> nonEmptyText,
      "noIteration" -> nonEmptyText
    )(ExpSettings.apply)(ExpSettings.unapply)
  )

  val expResultsForm = Form(
    mapping(
      "DataSize" -> seq(nonEmptyText),
      "ClusterSize" -> seq(nonEmptyText),
      "NoIteration" -> seq(nonEmptyText),
      "Algorithm" -> seq(nonEmptyText),
      "TimeUsed" -> seq(nonEmptyText)
    )(ExpResults.apply)(ExpResults.unapply)
  )

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def setExp = Action {
    Ok(views.html.form(expSettingForm))
  }

  def runExp = Action {
    implicit request => {
      val expData = expSettingForm.bindFromRequest().get
      if (Experiment.status == NotRunning) {
        val expResults = Experiment.experimentResult(expData)
        Ok(views.html.table(expResults)(true))
      }else
        Ok(views.html.retry())
    }
  }

  def saveResults = Action {
    implicit request => {
      val expResults = expResultsForm.bindFromRequest().get
      Experiment.saveResults(expResults)
      Ok(views.html.table(expResults.list)(false))
    }
  }

  def showAll = Action {
    val results = Experiment.getAllResults
    Ok(views.html.table(results)(false))
  }
}