package spotification.infra

import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.httpclient.HttpClientModule
import spotification.infra.httpclient.HttpClientModule.HttpClientService
import zio.Layer

object InfraModule {
  val layer: Layer[Throwable, HttpClientService] =
    ExecutionContextModule.layer >>> HttpClientModule.layer
}
