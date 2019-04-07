package io.julius.justus.viewmodel

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import io.julius.justus.ChatService
import io.julius.justus.model.Message
import io.julius.justus.model.MessageType

class ChatViewModel(context: Context) : ViewModel() {

    private val chatService: ChatService = ChatService(context.applicationContext)
    var messages = MediatorLiveData<MutableList<Message>>()

    val userId = generateRandomId()
    lateinit var subscriptionTopic: String

    init {
        // Set chat service client id
        chatService.clientId = userId

        // Initialize messages
        messages.value = mutableListOf()

        // subscribe to the source of chat message stream
        messages.addSource(chatService.message) { newMessage ->
            // Check if the items contain a message with the id of the incoming message.
            // If it does contain, that means the message has been edited or deleted, so update the existing item accordingly
            for (item in (messages.value as MutableList<Message>)) {
                // Since all notification messages have the same id, we will check if the createdAt is not -1.
                // So as not to interfere with notification messages
                if (item.id == newMessage.id && item.createdAt != "-1".toLong()) {
                    messages.value!![messages.value!!.indexOf(item)] = newMessage

                    // Post the value of the updated list so all observers can get notified
                    messages.postValue(messages.value)
                    return@addSource
                }
            }

            messages.value?.add(newMessage)

            // Post the value of the updated list so all observers can get notified
            messages.postValue(messages.value)
        }
    }

    fun subscribeChatService() {
        // Initialize messages
        messages.value = mutableListOf()
        
        chatService.subscriptionTopic = subscriptionTopic
        chatService.initialize()
    }

    // Called when this view model is no longer needed
    override fun onCleared() {
        super.onCleared()

        // Remove source to mediator live data
        messages.removeSource(chatService.message)

        // Unsubscribe and disconnect the chat service
        chatService.unsubscribe()
    }

    fun pushMessage(content: String) {
        val message = Message(
            id = generateRandomId(),
            message = content,
            senderId = userId,
            createdAt = System.currentTimeMillis(),
            type = MessageType.SENT
        )
        chatService.pushMessage(message)
    }

    // Use the same function name for editing message so that the send button action
    fun pushEditedMessage(message: Message) {
        chatService.pushMessage(message)
    }

    fun deleteMessage(message: Message) {
        message.apply {
            this.isDeleted = true
        }
        chatService.pushMessage(message)
    }

    companion object {
        // Character pool for generating random user id
        private val characterPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        // Simple kotlin util variable to return random string characters
        private fun generateRandomId() = (1..8)
            .map { kotlin.random.Random.nextInt(0, characterPool.size) }
            .map(characterPool::get)
            .joinToString("")
    }
}