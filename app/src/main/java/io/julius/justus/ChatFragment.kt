package io.julius.justus


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.julius.justus.model.Message
import io.julius.justus.viewmodel.ChatViewModel
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * A simple [Fragment] subclass.
 *
 */
class ChatFragment : Fragment() {

    lateinit var adapter: ChatAdapter

    lateinit var chatViewModel: ChatViewModel

    // Variable to hold editing message. When null, then there is no message being edited
    private var editedMessage: Message? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatViewModel = ViewModelProviders.of(activity!!).get(ChatViewModel::class.java)
        chatViewModel.messages.observe(this, Observer {
            adapter.updateMessages(it)
        })

        adapter = ChatAdapter(chatViewModel.messages.value!!)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tell if this instance was created cause of a screen rotation
        if (savedInstanceState == null) {
            // Connect and subscribe the chat service if we haven't subscribed before
            chatViewModel.subscribeChatService()
        }

        toolbar.title = chatViewModel.subscriptionTopic

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // We don't want to handle move actions, so we return false
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // LEFT swipe will be for editing a message while RIGHT swipe will be to delete a message
                if (direction == ItemTouchHelper.LEFT) {
                    editedMessage = adapter.items[viewHolder.adapterPosition]
                    field_message.setText(editedMessage?.message)
                } else if (direction == ItemTouchHelper.RIGHT) {
                    chatViewModel.deleteMessage(adapter.items[viewHolder.adapterPosition])
                }
            }

            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val position = viewHolder.adapterPosition
                val chatItem = adapter.items[position]

                // Get time difference between now and the time the chat item was sent.
                val timeDifference = System.currentTimeMillis() - chatItem.createdAt
                // Check if time difference is less than 4 minutes (240000) and senderId is current user's id
                if (timeDifference < FOUR_MINUTES && chatItem.senderId == chatViewModel.userId && !chatItem.isDeleted) {
                    return super.getSwipeDirs(recyclerView, viewHolder)
                }
                return 0
            }
        }).attachToRecyclerView(recycler_view)

        recycler_view.adapter = adapter

        send_button.setOnClickListener {
            // Do nothing if the message input is blank
            if (field_message.text.toString().trim().isEmpty()) {
                return@setOnClickListener
            }

            if (editedMessage != null) {
                editedMessage?.apply {
                    this.message = field_message.text.toString()
                }
                chatViewModel.pushEditedMessage(editedMessage!!)
                editedMessage = null
            } else {
                chatViewModel.pushMessage(field_message.text.toString())
            }

            field_message.setText("")
        }
    }

    companion object {
        // Constant to represent threshold for message to be editable and deletable
        const val FOUR_MINUTES = 240000
    }

}
