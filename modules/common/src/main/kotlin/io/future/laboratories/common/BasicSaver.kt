package io.future.laboratories.common

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

public class BasicSaver<T>(
    private val fromString: (String) -> T,
    private val toString: (T) -> String,
) : Saver<SnapshotStateList<T>, String> {
    override fun restore(value: String): SnapshotStateList<T> {
        return value
            .split(SEPARATOR)
            .filter { it.isNotBlank() }
            .map { fromString(it) }
            .toMutableStateList()
    }

    public override fun SaverScope.save(value: SnapshotStateList<T>): String {
        return value.joinToString(separator = SEPARATOR) { toString(it) }
    }

    public companion object {
        public const val SEPARATOR: String = ", "
    }
}