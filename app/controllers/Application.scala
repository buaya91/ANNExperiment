package controllers

import models.{ExpSettings, ExpVariable}
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

object Application extends Controller {

  val expSettingForm = Form(
    mapping(
      "dataSize" -> text,
      "clusterSize" -> text,
      "noIteration" -> text
    )(ExpSettings.apply)(ExpSettings.unapply)
  )

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def runExp = Action {
    request => {
      Ok
    }
  }

}