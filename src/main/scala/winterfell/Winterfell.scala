// Copyright (c) 2017, All rights reserved.

package winterfell

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.actor.Props
import akka.stream.ActorMaterializer
import winterfell.networking.mqtt.UserClient
import winterfell.networking.udp.UDPListener
import winterfell.networking.udp.UDPMessageHandler
import winterfell.networking.udp.UDPReceiver

import scala.io.StdIn

object Winterfell extends App {

  private def printUsage: Unit = {
    println(
      """Winterfell Gateway
        |Copyright (c) 2017 Hoang Hong Khang
        |
        |Usage: java -jar winterfell.jar [brokerURI] [user:pwd] [topic] [port]
        |     | java -jar winterfell.jar [--broker brokerURI] [--userpwd user:pwd] [--topic topicName] [--port port]
        |   --broker  specify brokerURI (default:)
        |   --userpwd    specify MQTT username and password (default: user01:123456)
        |   --topic   specify topic name to subscribe (default bus50)
        |   --port    specify port to listen (default 6789)
        |   [brokerURI] [user:pwd] [topic] [port] ex. ssl://m11.cloudmqtt.com:22133 user01:123456 bus50 6789
        |   --help    this message
      """.stripMargin)
    System.exit(0)
  }

  // scalastyle:off cyclomatic.complexity multiple.string.literals
  private def parseArgs(map: Map[String, Any], list: List[String]): Map[String, Any] = {
    list match {
      case Nil => map
      case "--broker" :: value :: tail => parseArgs(map ++ Map("broker" -> value.trim), tail)
      case "--userpwd" :: value :: tail =>
        val opt = value.split(':')
        parseArgs(map ++ Map("user" -> opt.apply(0), "pwd" -> opt.apply(1)), tail)
      case "--topic" :: value :: tail => parseArgs(map ++ Map("topic" -> value.trim), tail)
      case "--port" :: value :: tail => parseArgs(map ++ Map("port" -> value.toInt), tail)
      case broker :: userPwd :: topic :: port :: Nil =>
        val opt1 = userPwd.split(':')
        if (opt1.length == 2) {
          Map(
            "broker" -> broker.trim,
            "user" -> opt1(0),
            "pwd" -> opt1(1),
            "topic" -> topic.trim,
            "port" -> port.toInt
          )
        } else {
          printUsage
          map
        }
      case option :: tail =>
        println(s"Unknown option $option")
        printUsage
        map
    }
  }
  // scalastyle:on cyclomatic.complexity multiple.string.literals

  override def main(args: Array[String]): Unit = {
    if (args.contains("--help")) {
      printUsage
    }
    implicit val actorSystem = ActorSystem("winterfell")
    implicit val materializer = ActorMaterializer()

    val options: Map[String, Any] = parseArgs(Map(), args.toList)
    // scalastyle:off token
    val brokerURI = options.getOrElse("broker", WinterfellConfig.brokerURI).asInstanceOf[String]
    val user = options.getOrElse("user", WinterfellConfig.defaultUsername).asInstanceOf[String]
    val pass = options.getOrElse("pwd", WinterfellConfig.defaultUserPwd).asInstanceOf[String]
    val gatewayTopic = options.getOrElse("topic", WinterfellConfig.gatewayTopic).asInstanceOf[String]
    val port = options.getOrElse("port", WinterfellConfig.PORT).asInstanceOf[Int]
    // scalastyle:off token

    val udpAddress = new InetSocketAddress(port)
    val mqttClient = UserClient(brokerURI, user, pass, gatewayTopic)
    val handler = actorSystem.actorOf(
      Props(classOf[UDPMessageHandler], mqttClient),
      name = "MessageHandler"
    )
    val receiver = actorSystem.actorOf(Props(classOf[UDPReceiver], handler), name = "Receiver" )
    val listener = actorSystem.actorOf(
      Props(classOf[UDPListener], udpAddress, receiver),
      name = "Listener"
    )
    println(Console.YELLOW + "\nWelcome to Winterfell.")
    listener ! UDPListener.Bind

    println("Press any key to terminal service.")
    StdIn.readLine()
    listener ! UDPListener.Unbind
    mqttClient.disconnect
    actorSystem.terminate()
    System.exit(0)
  }
}
