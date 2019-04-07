package io.julius.justus.home


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation

import io.julius.justus.R
import io.julius.justus.viewmodel.ChatViewModel
import kotlinx.android.synthetic.main.fragment_topic.*

/**
 * A simple [Fragment] subclass.
 *
 */
class TopicFragment : Fragment() {

    lateinit var chatViewModel: ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatViewModel = ViewModelProviders.of(activity!!).get(ChatViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_topic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        proceed_button.setOnClickListener {
            if (field_topic.text.toString().trim().isEmpty()) {
                Toast.makeText(context, "Please enter a Topic", Toast.LENGTH_SHORT).show()
            } else {
                chatViewModel.subscriptionTopic = field_topic.text.toString().trim()
                Navigation.findNavController(activity!!, R.id.navigation_host_fragment).navigate(R.id.action_topicFragment_to_chatFragment)
            }
        }
    }
}
