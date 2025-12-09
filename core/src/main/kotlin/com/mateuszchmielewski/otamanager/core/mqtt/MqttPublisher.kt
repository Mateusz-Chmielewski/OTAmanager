package com.mateuszchmielewski.otamanager.core.mqtt

import org.springframework.integration.mqtt.support.MqttHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class MqttPublisher(
    private val mqttOutboundChannel: MessageChannel
) {
    fun publish(topic: String, payload: String) {
        val message: Message<String> = MessageBuilder
            .withPayload(payload)
            .setHeader(MqttHeaders.TOPIC, topic)
            .build()

        mqttOutboundChannel.send(message)
    }
}