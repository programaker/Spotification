package spotification.infra.httpserver

import spotification.infra.concurrent.ExecutionContextZIO.ExecutionContextService
import spotification.infra.config.ConfigZIO.ServerConfigService
import spotification.presentation.PresentationZIO.PresentationEnv

object HttpServerZIO {
  type HttpServerEnv = ServerConfigService with ExecutionContextService with PresentationEnv
}
