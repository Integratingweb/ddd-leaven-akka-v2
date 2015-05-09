package ecommerce.sales.app

import _root_.akka.cluster.Cluster
import akka.actor.{ActorRef, ActorSystem}
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.sales.{OrderSaga, Reservation}
import org.slf4j.LoggerFactory._
import pl.newicom.dddd.office.Office._
import pl.newicom.dddd.cluster._
import pl.newicom.dddd.process.SagaSupport.registerSaga

object SalesBackendApp extends App with SalesBackendConfiguration {
  val appName = this.getClass.getSimpleName


  lazy val log = getLogger(this.getClass.getName)

  log.info("----------------------------------------------------------------")
  log.info(s"Starting up $appName")
  log.info("----------------------------------------------------------------")

  val config: Config = ConfigFactory.load()
  implicit val system = ActorSystem("sales", config)

  var _reservationOffice: ActorRef = null
  def reservationOffice = _reservationOffice.path

    joinCluster()
    openOffices()


  def openOffices(): Unit = {
    _reservationOffice = office[Reservation]
    registerSaga[OrderSaga]
  }

  /**
   * Join the cluster with the specified seed nodes and block until termination
   */
  def joinCluster(): Unit = {
    val seedList = seeds(config)
    log.info(s"Joining cluster with seed nodes: $seedList")
    Cluster(system).joinSeedNodes(seedList.toSeq)
  }

  Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
    def run = {
      log.info("----------------------------------------------------------------")
      log.info(s"Shutting down  $appName")
      log.info("----------------------------------------------------------------")
      system.terminate()
    }
  }))

}