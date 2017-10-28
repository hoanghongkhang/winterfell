
package winterfell.networking.mqtt

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder

final case class Message(
  id: String,
  longtitude: Double,
  latitude: Double,
  speed: Double,
  number: Int,
  time: Long
)

object Message {
  implicit val encoder: Encoder[Message] = deriveEncoder
  implicit val decoder: Decoder[Message] = deriveDecoder
}
