// Copyright (c) 2017, All rights reserved.

package winterfell.networking.udp

import java.net.InetSocketAddress

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.io.IO
import akka.io.Udp
import winterfell.networking.udp.UDPListener.Unbind

// scalastyle:off underscore.import
import winterfell.networking.udp.UDPListener.Bind
// scalastyle:on underscore.import

class UDPListener(
  udpAddress: InetSocketAddress,
  private val receiver: ActorRef
) extends Actor {
  // scalastyle:off import.grouping
  import context.system
  // scalastyle:on import.grouping

  def receive: Receive = {
    case Bind => IO(Udp) ! Udp.Bind(self, udpAddress)
    case Udp.Bound(local) =>
      println(s"service is listening on ${udpAddress.getPort}")
      context.become(ready(sender()))
  }

  def ready(socket: ActorRef): Receive = {
    case Udp.Received(data, remote) =>
      receiver ! Udp.Received(data, remote)
    case Unbind  =>
      socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
  }
}

object UDPListener {
  object Bind
  object Unbind
}
