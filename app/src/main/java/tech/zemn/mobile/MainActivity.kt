package tech.zemn.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Button
import androidx.compose.material.Text
import dagger.hilt.android.AndroidEntryPoint
import tech.zemn.mobile.ui.theme.ZemnTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<SharedViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZemnTheme {
                Button(
                    onClick = {
                        viewModel.foo()
                    }
                ){
                    Text(text = "Scan")
                }
            }
        }
    }
}