# dynatrace-kafka-agent
A solution to provide end to end coverage for clients accessing [Kafka](https://kafka.apache.org/).

## Objective
[Kafka](https://kafka.apache.org/) let's you publish and subscribe to streams of records. In this respect it is similar to a message queue or enterprise messaging system.
Unfortunately there is no out of the box visibility (yet) when monitoring your application with [Dynatrace](http://www.dynatrace.com/en/free-apm-tools.html#dt-free-trial).

This solution utilizes the Dynatrace ADK for Java in order to change that.

## Build Instructions
* This solution represents an Eclipse Project created for [Eclipse Mars](https://eclipse.org/mars/)
* Import the existing Project into your Eclipse Workspace
* In order to compile and link the ```dynatrace-kafka-agent.jar``` the Ant Build Script ```<PROJECT>/build.xml``` packages up everything.
* A precompiled version of ```dynatrace-kafka-agent.jar``` is available within [this repository](https://github.com/Dynatrace-Reinhard-Pilz/dynatrace-kafka-agent/blob/master/dynatrace-kafka-agent.jar?raw=true) for download and should also be contained within your Eclipse Project

## Installation Instructions
* Rebuild ```dynatrace-kafka-agent.jar``` or download it from [GitHub](https://github.com/Dynatrace-Reinhard-Pilz/dynatrace-kafka-agent/blob/master/dynatrace-kafka-agent.jar?raw=true)
  - The following instructions assume that a folder ```/opt/dynatrace-kafka-agent``` contains ```dynatrace-kafka-agent.jar```
* The Kafka Agent needs to be specified via JVM Arguments *before* the ```–agentpath``` Argument for the dynaTrace Agent
  - Example for Linux: ```java -javaagent:/opt/dynatrace-kafka-agent/dynatrace-kafka-agent.jar -agentpath:/opt/dynatrace-6.2/agent/lib64/libdtagent.so=name=<agentname>```
  
## Restrictions
Clients sending data to Kafka are required to use ```org.apache.kafka.clients.producer.KafkaProducer.send(...)``` in order to benefit from this solution.
Clients receiving data from Kafka are required to use ```org.apache.kafka.clients.consumer.KafkaConsumer.poll(..)``` and iterate over the resulting ConsumerRecords.
Because the solution is utilizing already existing functionality for MQ Series, the intermediate nodes within the produced PurePaths are falsly pointing out MQ Series traffic.
The benefit of that approach is that the user configurable MQ Series Entry Point Sensor can get utilized in order to continue client side PurePaths after polling for Consumer Records.
