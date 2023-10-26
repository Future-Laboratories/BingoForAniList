package io.future.laboratories.anilistbingo.annotation

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "Only use if you know what you are doing."
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
)
public annotation class RestrictedApi
