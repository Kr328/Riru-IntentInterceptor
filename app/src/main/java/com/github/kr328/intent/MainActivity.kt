package com.github.kr328.intent

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.kr328.intent.ui.AppTheme

class MainActivity : BaseActivity() {
    @Composable
    override fun Content() {
        Greeting(name = "android")
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        Greeting("Android")
    }
}