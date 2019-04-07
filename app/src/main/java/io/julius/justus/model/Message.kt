package io.julius.justus.model

// Default values for senderId and createdAt just so we don't have to specify it for notification messages
data class Message(
    // The message content
    var message: String,
    // Each message has an id so that we can delete and edit specific messages
    var id: String = "",
    // Sender id of the message to associate authority on messages such as the delete and edit operations
    var senderId: String = "",
    // The time a message is created, so that we can set time limits on messages that can be deleted and edited
    var createdAt: Long = -1,
    // Boolean to tell if a message has been deleted.
    // Since we don't have our own instance of MQTT, and haven't implemented local data storage for simplicity,
    // This field will help to make deleted messages not editable and display differently from other messages
    var isDeleted: Boolean = false,
    // Message type to update relevant UI sections
    var type: MessageType = MessageType.NOTIFICATION
)