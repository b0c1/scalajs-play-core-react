import com.softwaremill.macwire._
import controllers.{ApiController, Assets, DemoController}
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import play.api.routing.sird._
import play.api.{Application, ApplicationLoader, BuiltInComponents, BuiltInComponentsFromContext}

/**
  * Global application context
  */
class GlobalApplicationLoader extends ApplicationLoader {
  override def load(context: Context): Application = (new BuiltInComponentFromContextWithPlayWorkaround(context) with ApplicationComponents).application
}

abstract class BuiltInComponentFromContextWithPlayWorkaround(context: Context) extends BuiltInComponentsFromContext(context) {

  import play.api.inject.{Injector, NewInstanceInjector, SimpleInjector}
  import play.api.libs.Files.DefaultTemporaryFileCreator

  lazy val defaultTemporaryFileCreator = new DefaultTemporaryFileCreator(applicationLifecycle)

  override lazy val injector: Injector = new SimpleInjector(NewInstanceInjector) + router + crypto + httpConfiguration + defaultTemporaryFileCreator
}

trait ApplicationComponents extends BuiltInComponents with Controllers {
  lazy val assets: Assets = wire[Assets]
  lazy val router: Router = Router.from {
    case GET(p"/") => applicationController.index
  }
}

trait Controllers extends BuiltInComponents {
  lazy val applicationController = wire[DemoController]
  lazy val apiController = wire[ApiController]
}
