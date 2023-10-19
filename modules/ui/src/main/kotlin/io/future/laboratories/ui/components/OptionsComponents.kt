package io.future.laboratories.ui.components

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.core.content.edit

@JvmInline
public value class OptionKey(public val key: String)

public abstract class OptionData<T : Any> {
    internal abstract val preferences: SharedPreferences
    internal abstract val key: OptionKey
    internal abstract val name: @Composable () -> String
    internal abstract val defaultValue: T

    @Suppress("LeakingThis")
    public var currentValue: T = defaultValue
        get() = loadData()
        internal set

    public fun toPair(): Pair<OptionKey, OptionData<T>> {
        return key to this
    }

    @Composable
    public abstract operator fun invoke()

    internal abstract fun loadData(): T

    internal abstract fun T.saveData()
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
) : OptionData<Boolean>() {
    @Composable
    override fun invoke() {
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
) : OptionData<String>() {
    @Composable
    override fun invoke() {
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