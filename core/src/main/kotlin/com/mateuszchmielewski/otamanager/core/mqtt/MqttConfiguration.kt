package com.mateuszchmielewski.otamanager.core.mqtt

import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.core.MqttPahoClientFactory
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler

@Configuration
class MqttConfiguration {

    @Value("\${mqtt.broker.url:tcp://localhost:1883}")
    private lateinit var brokerUrl: String

    @Value("\${mqtt.client.id:spring-mqtt-client}")
    private lateinit var clientId: String

    @Bean
    fun mqttClientFactory(): MqttPahoClientFactory {
        val factory = DefaultMqttPahoClientFactory()
        val options = MqttConnectOptions()
        options.serverURIs = arrayOf(brokerUrl)
        options.isCleanSession = true
        factory.connectionOptions = options
        return factory
    }

    @Bean
    fun mqttOutboundChannel(): MessageChannel {
        return DirectChannel()
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    fun mqttOutbound(mqttClientFactory: MqttPahoClientFactory): MessageHandler {
        val messageHandler = MqttPahoMessageHandler(clientId, mqttClientFactory)
        messageHandler.setAsync(true)
        messageHandler.setDefaultTopic("notifications")
        return messageHandler
    }
}