package io.julius.justus.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import io.julius.justus.viewmodel.ChatViewModel
import io.julius.justus.R
import io.julius.justus.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create view model with activity scope so that hosted fragment will have access to the same instance
        ViewModelProviders.of(this, ViewModelFactory(this)).get(ChatViewModel::class.java)
    }
}
