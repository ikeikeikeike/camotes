package tasks

import play.api.{ Configuration, Environment }

class Module extends play.api.inject.Module {
  def bindings(environment: Environment, configuration: Configuration) = {
    Seq(
      bind[SlackNotify].toSelf.eagerly
    )
  }
}
