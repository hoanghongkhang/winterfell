# ❄ winterfell ❄

A gateway client application using CloudMQTT api.

## Usage

### For help
go to directory **/out/artifacts/winterfell_jar/**

```bash
$ java -jar winterfell.jar --help
```
output:

```bash
Winterfell Gateway
Copyright (c) 2017 Hoang Hong Khang

Usage: java -jar winterfell.jar [brokerURI] [user:pwd] [topic] [port]
     | java -jar winterfell.jar [--broker brokerURI] [--userpwd user:pwd] [--topic topicName] [--port port]
   --broker  specify brokerURI (default:)
   --userpwd    specify MQTT username and password (default: user01:123456)
   --topic   specify topic name to subscribe (default bus50)
   --port    specify port to listen (default 6789)
   [brokerURI] [user:pwd] [topic] [port] ex. ssl://m11.cloudmqtt.com:22133 user01:123456 bus50 6789
   --help    this message
```

## Star service
### Start service with default configuration
#### Using sbt
run this command in the `root directory`
```bash
$ sbt > run
```
#### Using java 
run this command in `/out/artifacts/winterfell_jar/`
```
$ java -jar winterfell.jar
```
### Start service with my configuration
```bash
$ java -jar winterfell.jar ssl://m11.cloudmqtt.com:22133 user01:123456 bus50 6789
```