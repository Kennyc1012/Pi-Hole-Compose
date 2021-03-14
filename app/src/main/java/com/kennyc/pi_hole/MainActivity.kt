package com.kennyc.pi_hole

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.kennyc.data.pi_hole.model.PiholeSummary
import com.kennyc.data.pi_hole.model.PiholeSystemStatus
import com.kennyc.pi_hole.ui.theme.PiUi.buildUi
import com.kennyc.pi_hole.ui.theme.PiUi.piholeScreen
import com.kennyc.pi_hole.ui.theme.PiholeComposeTheme
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel by viewModels<MainViewModel> { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = applicationContext as PiholeApp
        app.component.inject(this)
        setContent {
            PiholeComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    piholeScreen(viewModel)
                }
            }
        }
    }
}