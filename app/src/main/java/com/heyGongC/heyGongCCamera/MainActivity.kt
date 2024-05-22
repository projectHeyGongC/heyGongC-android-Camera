package com.heyGongC.heyGongCCamera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.heyGongC.heyGongCCamera.databinding.ActivityMainBinding
import com.heyGongC.heyGongCCamera.ui.streaming.StreamingFragmentDirections
import com.heyGongC.heyGongCCamera.util.Constants.ACTION
import com.heyGongC.heyGongCCamera.util.Constants.CONTENT
import com.heyGongC.heyGongCCamera.util.Constants.FLASH
import com.heyGongC.heyGongCCamera.util.Constants.REMOTE_EXECUTION
import com.heyGongC.heyGongCCamera.util.Constants.REMOTE_SHUTDOWN
import com.heyGongC.heyGongCCamera.util.Constants.SOUND_SENSING
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moveToFirstScreen()
        handleNotificationIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val action = intent?.getStringExtra(ACTION)
        val content = intent?.getStringExtra(CONTENT)
        when {
            action == REMOTE_EXECUTION -> {}
            action == REMOTE_SHUTDOWN -> {
                finish()
            }
        }
    }

    private fun moveToFirstScreen() {
        val navController =
            supportFragmentManager.findFragmentById(R.id.homeNavHost)?.findNavController()
        lifecycleScope.launch {
            mainViewModel.hasLocalAccessToken.collectLatest {
                if (!it) {
                    val action = StreamingFragmentDirections.actionStreamingFragmentToTermsFragment()
                    navController?.navigate(action)
                }
            }
        }
        lifecycleScope.launch {
            mainViewModel.getLocalAccessToken()
        }
    }
}