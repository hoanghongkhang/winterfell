// Copyright (c) 2017, All rights reserved.

package winterfell.networking.mqtt

import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

// scalastyle:off underscore.import
// scalastyle:on underscore.import

class BaseClient(
  private val brokerURI: String,
  private val username: String,
  private val password: String
) {

  private[this] val settings = new MqttConnectOptions()

  private[this] def setAuth(username: String, password: String): Unit = {
    if (username.length > 0 && password.length > 0) {
      settings.setUserName(username)
      settings.setPassword(password.toArray)
    }
  }

  private[this] lazy val connector: MqttClient = {
    val client = new MqttClient(brokerURI, username)
    client
  }

  def login: Boolean = {
    setAuth(username, password)
    connector.connect(settings)
    connector.isConnected
  }

  def disconnect: Unit = {
    connector.disconnect()
  }

  def subscribe(topic: String): Unit = {
    println(s"subscribe to $topic")
    connector.subscribe(topic)
  }

  def unsubscribe(topic: String): Unit = {
    println(s"unsubscribe to $topic")
    connector.unsubscribe(topic)
  }

  def publish(topic: String, msg: Message): Unit = {
    connector.publish(
      topic,
      new MqttMessage(
        Message.encoder.apply(msg).noSpaces.getBytes()
      )
    )
  }

  protected def setMqttCallback(callback: MqttCallback): Unit = {
    connector.setCallback(callback)
  }

  def isConnected: Boolean = connector.isConnected

}
