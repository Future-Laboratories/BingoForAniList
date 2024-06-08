package io.future.laboratories.common

//region lambda

public operator fun (() -> Unit).plus(rhs: () -> Unit): () -> Unit = {
    this()
    rhs()
}

//endregion
