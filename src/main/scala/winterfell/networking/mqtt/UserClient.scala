
package winterfell.networking.mqtt

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

import scala.io.StdIn

class UserClient(
  brokerURI: String,
  username: String,
  password: String,
  topic: String
) extends BaseClient(brokerURI, username, password) {

  private[this] lazy val mqttCallback = new MqttCallback {

    override def deliveryComplete(token: IMqttDeliveryToken): Unit = {
    }

    override def connectionLost(cause: Throwable): Unit = {
      println("connection lost")
      println("Press any key to exit")
      StdIn.readLine()
      System.exit(1)
    }

    override def messageArrived(topic: String, json: MqttMessage): Unit = {
      val message = io.circe.parser.decode[Message](json.toString).toTry.get
    }
  }

  def publish(msg: Message): Unit = {
    this.publish(topic, msg)
  }

  def subscribe(): Unit = {
    this.subscribe(topic)
  }

  override def login(): Boolean = {
    super.login
    setMqttCallback(mqttCallback)
    this.isConnected
  }
}

object UserClient{
  def apply(
    brokerURI: String,
    username: String,
    password: String,
    topic: String
  ): UserClient = new UserClient(brokerURI, username, password, topic)
}
