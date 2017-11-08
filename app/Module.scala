import play.api.{ Configuration, Environment }
import play.api.Mode._

class Module extends play.api.inject.Module {

  def bindings(environment: Environment, configuration: Configuration) = {
    Seq.empty
  }
}
