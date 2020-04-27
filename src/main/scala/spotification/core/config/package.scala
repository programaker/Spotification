package spotification.core

import io.estatico.newtype.macros.newtype

package object config {
  @newtype case class Directory(value: String)
  @newtype case class Bytes(value: Long)
}
