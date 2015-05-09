package ecommerce.sales.app

import akka.actor._
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory._

object SalesFrontApp extends App {
  lazy val log = getLogger(this.getClass.getName)
  val appName = this.getClass.getSimpleName

  log.info("----------------------------------------------------------------")
  log.info(s"Starting up $appName")
  log.info("----------------------------------------------------------------")

  private val config: Config = ConfigFactory.load()
  implicit private val system = ActorSystem("sales-front", config)

  system.actorOf(SalesFrontAppSupervisor.props, "sales-front-supervisor")


  Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
    def run = {
      log.info("----------------------------------------------------------------")
      log.info(s"Shutting down  $appName")
      log.info("----------------------------------------------------------------")
      system.terminate()
    }
  }))
}

object SalesFrontAppSupervisor {
  def props = Props(new SalesFrontAppSupervisor)
}

class SalesFrontAppSupervisor extends Actor with ActorLogging with SalesFrontConfiguration {

  override val supervisorStrategy = SupervisorStrategy.stoppingStrategy

  context.watch(createHttpService())

  override def receive: Receive = {
    case Terminated(ref) =>
      log.warning("Shutting down, because {} has terminated!", ref.path)
      context.system.terminate()
  }

  protected def createHttpService(): ActorRef = {
    import httpService._
    context.actorOf(HttpService.props(interface, port, askTimeout), "http-service")
  }
}