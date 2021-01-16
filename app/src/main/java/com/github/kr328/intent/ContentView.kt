package com.github.kr328.intent

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Providers
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.AmbientConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.kr328.intent.ui.AmbientInsets
import com.github.kr328.intent.ui.AppTheme
import com.github.kr328.intent.ui.Insets

class ContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context = context, attrs = attrs, defStyleAttr = defStyleAttr) {
    private val configuration: MutableState<Configuration> = mutableStateOf(resources.configuration)
    private val content: MutableState<@Composable () -> Unit> = mutableStateOf({})
    private val insets: MutableState<Insets> = mutableStateOf(Insets(0.dp, 0.dp, 0.dp, 0.dp))

    fun setConfiguration(configuration: Configuration) {
        this.configuration.value = configuration
    }

    fun setContent(content: @Composable () -> Unit) {
        this.content.value = content
    }

    @Composable
    override fun Content() {
        Providers(
            AmbientInsets provides insets.value,
            AmbientConfiguration provides configuration.value,
        ) {
            AppTheme {
                Surface {
                    content.value()
                }
            }
        }
    }

    override fun onApplyWindowInsets(ins: WindowInsets): WindowInsets? {
        val compat = WindowInsetsCompat.toWindowInsetsCompat(ins)
        val insets = compat.systemWindowInsets

        val rInsets = if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            Insets(
                px2dp(insets.left.toFloat()),
                px2dp(insets.top.toFloat()),
                px2dp(insets.right.toFloat()),
                px2dp(insets.bottom.toFloat()),
            )
        } else {
            Insets(
                px2dp(insets.right.toFloat()),
                px2dp(insets.top.toFloat()),
                px2dp(insets.left.toFloat()),
                px2dp(insets.bottom.toFloat()),
            )
        }

        this.insets.value = rInsets

        return compat.consumeSystemWindowInsets().toWindowInsets()
    }

    private fun px2dp(value: Float): Dp {
        return Dp(value / resources.displayMetrics.density)
    }
}