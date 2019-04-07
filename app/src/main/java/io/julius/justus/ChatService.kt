package io.julius.justus

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import io.julius.justus.model.Message
import io.julius.justus.model.MessageType
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class ChatService(var context: Context) : MqttCallbackExtended {

    private lateinit var mqttAndroidClient: MqttAndroidClient

    private val mqttServerUrl = "tcp://broker.mqttdashboard.com:1883"

    lateinit var clientId: String
    lateinit var subscriptionTopic: String

    var message = MutableLiveData<Message>()

    fun initialize() {
        mqttAndroidClient = MqttAndroidClient(context, mqttServerUrl, clientId)
        mqttAndroidClient.setCallback(this)

        val options = MqttConnectOptions()
        options.apply {
            connectionTimeout = 10000
            isAutomaticReconnect = true
            isCleanSession = false
            keepAliveInterval = 600000
        }

        mqttAndroidClient.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                message.postValue(Message("Success connecting to server..."))
                subscribeToTopic()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                message.postValue(Message("Connection Failure: ${exception?.localizedMessage}"))
            }
        })
    }

    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
        if (reconnect) {
            message.postValue(Message("Reconnected to server"))
            // Because Clean Session is true here, we need to re-subscribe
            subscribeToTopic()
        } else {
            message.postValue(Message("Connected to server"))
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        message?.let {
            val response = GsonBuilder().setLenient().create().fromJson(it.toString(), Message::class.java)
            if (response.senderId != clientId) {
                response.apply { type = MessageType.RECEIVED }
            } else {
                response.apply { type = MessageType.SENT }
            }
            this.message.postValue(response)
        }
    }

    override fun connectionLost(cause: Throwable?) {
        message.postValue(Message("Connection lost"))
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        // Do nothing
    }

    fun subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    message.value = Message("Subscribed!")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    message.postValue(Message("Subscription Failed!"))
                }
            })
        } catch (ex: MqttException) {
            message.postValue(Message("Exception whilst subscribing"))
            ex.printStackTrace()
        }
    }

    fun unsubscribe() {
        mqttAndroidClient.unsubscribe(subscriptionTopic)
        mqttAndroidClient.unregisterResources()
        mqttAndroidClient.disconnect()
    }

    fun pushMessage(message: Message) {

        try {
            val outgoingMessage = MqttMessage()
            outgoingMessage.payload = GsonBuilder().setLenient().create().toJson(message).toByteArray()
            mqttAndroidClient.publish(subscriptionTopic, outgoingMessage)
        } catch (e: MqttException) {
            System.err.println("Error Publishing: " + e.message)
            e.printStackTrace()
        }

    }
}