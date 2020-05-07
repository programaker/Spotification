package spotification.application

import spotification.application.HttpClientModule.HttpClientService
import zio.Layer

object InfraModule {
  def layer: Layer[Throwable, HttpClientService] =
    ExecutionContextModule.layer >>> HttpClientModule.layer
}
