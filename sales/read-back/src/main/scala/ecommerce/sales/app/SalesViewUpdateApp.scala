package ecommerce.sales.app

import akka.actor._
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory
import ecommerce.sales.SalesViewUpdateService
import org.slf4j.LoggerFactory._

import scala.slick.driver.{JdbcProfile, PostgresDriver}

object SalesViewUpdateApp extends App {
  lazy val log = getLogger(this.getClass.getName)
  val appName = this.getClass.getSimpleName


  log.info("----------------------------------------------------------------")
  log.info(s"Starting up $appName")
  log.info("----------------------------------------------------------------")

  private val config = ConfigFactory.load()
  val system = ActorSystem("sales-view-update", config)

  implicit val profile: JdbcProfile = PostgresDriver
  system.actorOf(Props(new SalesViewUpdateService(config)), "sales-view-update-service")

  Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
    def run = {
      log.info("----------------------------------------------------------------")
      log.info(s"Shutting down  $appName")
      log.info("----------------------------------------------------------------")
      system.terminate()
    }
  }))
}

