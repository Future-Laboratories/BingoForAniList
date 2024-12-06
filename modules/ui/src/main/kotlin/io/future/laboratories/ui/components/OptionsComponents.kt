package io.future.laboratories.ui.components

import android.content.SharedPreferences
import android.graphics.Color.blue
import android.graphics.Color.green
import android.graphics.Color.red
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import kotlin.reflect.KProperty

@JvmInline
public value class OptionKey(public val key: String)

public abstract class OptionData<T : Any> {
    internal abstract val preferences: SharedPreferences
    internal abstract val key: OptionKey
    internal abstract val name: @Composable () -> String
    internal abstract val defaultValue: T
    internal abstract val isVisible: (() -> Boolean)?
    internal abstract val onValueChanged: ((T, T.() -> Unit) -> Unit)?

    public var currentValue: T by LazyMutableState()

    public fun toPair(): Pair<OptionKey, OptionData<T>> {
        return key to this
    }

    @Composable
    public operator fun invoke() {
        if (isVisible?.invoke() != false) {
            Layout()
        }
    }

    @Composable
    internal abstract fun Layout()

    internal abstract fun loadData(): T

    internal abstract fun T.saveData()

    private inner class LazyMutableState {
        private var _currentValue by mutableStateOf(defaultValue)

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            _currentValue = loadData()

            return _currentValue
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            if (_currentValue != value) {
                _currentValue = value

                if (onValueChanged == null) {
                    _currentValue.saveData()
                } else {
                    onValueChanged?.invoke(_currentValue) { saveData() }
                }
            }
        }
    }
}


public data class OptionGroup(
    val text: String,
    val options: List<OptionData<*>>,
    val isVisible: (() -> Boolean)? = null,
)

public data class BooleanOption(
    override val preferences: SharedPreferences,
    override val key: OptionKey,
    override val name: @Composable () -> String,
    override val defaultValue: Boolean,
    override val isVisible: (() -> Boolean)? = null,
    override val onValueChanged: ((Boolean, Boolean.() -> Unit) -> Unit)? = null,
) : OptionData<Boolean>() {
    @Composable
    override fun Layout() {
        OptionToggle(
            optionName = name(),
            initialValue = loadData(),
            onCheckedChange = { currentValue = it },
        )
    }

    override fun loadData(): Boolean = preferences.getBoolean(key.key, defaultValue)

    override fun Boolean.saveData(): Unit = preferences.edit {
        putBoolean(key.key, this@saveData)
    }
}

public data class DropdownOption(
    override val preferences: SharedPreferences,
    override val key: OptionKey,
    override val name: @Composable () -> String,
    override val defaultValue: String,
    internal val values: @Composable () -> Map<String, String>,
    override val isVisible: (() -> Boolean)? = null,
    override val onValueChanged: ((String, String.() -> Unit) -> Unit)? = null,
) : OptionData<String>() {
    @Composable
    override fun Layout() {
        OptionDropdown(
            optionName = name(),
            values = values(),
            initialValue = currentValue,
            onCheckedChange = { currentValue = it },
        )
    }

    override fun loadData(): String = preferences.getString(key.key, defaultValue) ?: defaultValue

    override fun String.saveData(): Unit = preferences.edit {
        putString(key.key, this@saveData)
    }
}

public data class ColorOption(
    override val preferences: SharedPreferences,
    override val key: OptionKey,
    override val name: @Composable () -> String,
    override val defaultValue: Color,
    override val isVisible: (() -> Boolean)?,
    override val onValueChanged: ((Color, Color.() -> Unit) -> Unit)? = null,
) : OptionData<Color>() {
    @Composable
    override fun Layout() {
        ColorPickerRow(
            optionName = name(),
            initialValue = currentValue,
            onCheckedChange = { currentValue = it }
        )
    }

    override fun loadData(): Color {
        return preferences.getString(key.key, null)?.let { Color(it.toULong()) } ?: defaultValue
    }

    override fun Color.saveData(): Unit = preferences.edit {
        putString(key.key, this@saveData.value.toString())
    }
}