// Copyright (c) 2017, All rights reserved.

package winterfell.networking.udp

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.io.Udp.Received
import io.circe.parser.decode
import winterfell.networking.mqtt.Message

import scala.util.Failure
import scala.util.Success
import scala.util.Try

class UDPReceiver(private val handler: ActorRef) extends Actor with ActorLogging {

  // scalastyle:off magic.number
  def receive: Receive= {
    case Received(data, from) =>
      println(s"Received data from $from: ${data.utf8String}")
      val msgs: Array[String] = data.utf8String.split('|')
      if (msgs.length == 6) {
        val pMsg = for {
          long <- Try(msgs(0).toDouble)
          lat <- Try(msgs(1).toDouble)
          speed <- Try(msgs(2).toDouble)
          id <- Try(msgs(3))
          number <- Try(msgs(4).toInt)
          time <- Try(msgs(5).toLong)
        } yield Message(id.toString, long, lat, speed, number, time)
        pMsg match {
          case Success(message) => handler ! HandleMessage(message)
          case Failure(error) => error.printStackTrace()
        }
      }
    case _ =>
  }
  // scalastyle:on magic.number
}