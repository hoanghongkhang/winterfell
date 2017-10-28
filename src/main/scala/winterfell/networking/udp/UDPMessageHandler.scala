// Copyright (c) 2017, All rights reserved.

package winterfell.networking.udp

import akka.actor.Actor
import winterfell.WinterfellConfig
import winterfell.networking.mqtt.UserClient

class UDPMessageHandler(private val mqttClient: UserClient) extends Actor {
  mqttClient.login()
  if (mqttClient.isConnected) {
    mqttClient.subscribe()
  }
  def receive: Receive = {
    case HandleMessage(message) =>
      if (mqttClient.isConnected) {
        mqttClient.publish(message)
      } else {
        println("Cannot connect to the CloudMQTT server")
      }
  }
}
