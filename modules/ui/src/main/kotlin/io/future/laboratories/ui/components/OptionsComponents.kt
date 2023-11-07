package io.future.laboratories.ui.components

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import kotlin.reflect.KProperty

@JvmInline
public value class OptionKey(public val key: String)

public abstract class OptionData<T : Any> {
    internal abstract val preferences: SharedPreferences
    internal abstract val key: OptionKey
    internal abstract val name: @Composable () -> String
    internal abstract val defaultValue: T
    internal abstract val isVisible: (() -> Boolean)?

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
            _currentValue = value
        }
    }
}


public data class OptionGroup(
    val text: String,
    val options: List<OptionData<*>>,
)

public data class BooleanOption(
    override val preferences: SharedPreferences,
    override val key: OptionKey,
    override val name: @Composable () -> String,
    override val defaultValue: Boolean,
    override val isVisible: (() -> Boolean)? = null,
) : OptionData<Boolean>() {
    @Composable
    override fun Layout() {
        OptionToggle(
            optionName = name(),
            initialValue = loadData(),
            onCheckedChange = { it.saveData() },
        )
    }

    override fun loadData(): Boolean = preferences.getBoolean(key.key, defaultValue)

    override fun Boolean.saveData(): Unit = preferences.edit {
        currentValue = this@saveData
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
) : OptionData<String>() {
    @Composable
    override fun Layout() {
        OptionDropdown(
            optionName = name(),
            values = values(),
            initialValue = currentValue,
            onCheckedChange = { it.saveData() },
        )
    }

    override fun loadData(): String = preferences.getString(key.key, defaultValue)!!

    override fun String.saveData(): Unit = preferences.edit {
        currentValue = this@saveData
        putString(key.key, this@saveData)
    }
}