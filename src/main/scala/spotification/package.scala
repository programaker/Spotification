import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.collection.MinSize
import eu.timepit.refined.string.MatchesRegex

package object spotification {
  type NonBlankString = MinSize[1] And Not[MatchesRegex["""^\s+$"""]]
}
