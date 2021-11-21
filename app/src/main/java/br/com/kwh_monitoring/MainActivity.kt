package br.com.kwh_monitoring

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {

    private val navController: NavController by lazy { findNavController(R.id.nav_host_fragment_container) }


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_KWhMonitoring)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}