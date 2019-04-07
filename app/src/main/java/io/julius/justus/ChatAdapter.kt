package io.julius.justus

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.julius.justus.model.Message
import io.julius.justus.model.MessageType
import kotlinx.android.synthetic.main.item_sent_message.view.*

class ChatAdapter(var items: MutableList<Message>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            1 -> ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_received_message,
                    parent,
                    false
                )
            )
            2 -> ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_sent_message,
                    parent,
                    false
                )
            )
            else -> ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_notification_message,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].type) {
            MessageType.NOTIFICATION -> 0
            MessageType.RECEIVED -> 1
            MessageType.SENT -> 2
        }
    }

    fun updateMessages(messages: MutableList<Message>) {
        // Update list of messages
        items = messages
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(message: Message) {
            // Italicize deleted message
            if (message.isDeleted) {
                itemView.text_message.text = itemView.context.getString(R.string.message_deleted)
                itemView.text_message.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC)
            } else {
                itemView.text_message.text = message.message
            }
        }
    }
}
