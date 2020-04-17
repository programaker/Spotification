package spotification.domain.config

import zio.Task

trait ConfigService {
  def readConfig: Task[AppConfig]
}
