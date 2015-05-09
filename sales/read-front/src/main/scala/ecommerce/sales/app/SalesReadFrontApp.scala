package ecommerce.sales.app

import akka.actor._
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import ecommerce.sales.{HttpService, SalesReadFrontConfiguration}
import org.slf4j.LoggerFactory._

object SalesReadFrontApp extends App {
  lazy val log = getLogger(this.getClass.getName)
  val appName = this.getClass.getSimpleName

  log.info("----------------------------------------------------------------")
  log.info(s"Starting up $appName")
  log.info("----------------------------------------------------------------")

  val config = ConfigFactory.load()
  val system = ActorSystem("sales-read-front", config)


  new SalesReadFrontConfiguration {
    override def config: Config = SalesReadFrontApp.this.config
    import httpService._
    system.actorOf(HttpService.props(interface, port, askTimeout), "http-service")
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