package com.example.newssourceapp.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.newssourceapp.R
import com.example.newssourceapp.databinding.ActivityMainBinding
import com.example.newssourceapp.ui.viewmodel.NewsListViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

/**
 * The activity of the application in this case acts a single user-focused thing to do, while the
 * responsibility of the screens is delegated to the fragments. As there's one single "activity" for
 * the user to do (scroll through news, click on them and read), there's no real necessity for this
 * app to have multiple activities.
 *
 * However, this activity is still of great value - it acts as an "enabler" (or an entry point) for
 * Dagger dependency injection.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    val viewModel: NewsListViewModel by viewModels()

    // region overridden methods
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        findViewById<Button>(R.id.crash_button).setOnClickListener {
            // Force a crash for analytics
            throw RuntimeException("Test Crash")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    // endregion

    fun logFirebaseRegistrationToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val message = "FCM Registration token: $token"
            Log.d(TAG, message)
            Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
        })
    }
}