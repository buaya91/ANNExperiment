package models

/**
 * Created by qingwei on 3/16/15.
 */

case object ExpStatus extends Enumeration{
  type ExpStatus = Value
  val Running, NotRunning = Value
}
