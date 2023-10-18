package io.future.laboratories.ui.components

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.core.content.edit

@JvmInline
public value class OptionKey(public val key: String)

public abstract class OptionData<T : Any> {
    internal abstract val preferences: SharedPreferences
    public abstract val key: OptionKey
    public abstract val name: @Composable () -> String
    public abstract val defaultValue: T
    public abstract var currentValue: T
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
    override var currentValue: Boolean = loadData()

    @Composable
    override fun invoke(): Unit = OptionToggle(
        optionName = name(),
        initialValue = loadData(),
        onCheckedChange = { it.saveData() },
    )

    override fun loadData(): Boolean = preferences.getBoolean(key.key, defaultValue)

    override fun Boolean.saveData(): Unit = preferences.edit {
        currentValue = this@saveData
        putBoolean(key.key, this@saveData)
    }
}