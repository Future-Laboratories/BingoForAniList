package io.future.laboratories.ui.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

public class ShakingState(
    private val strength: Strength,
    private val direction: Directions,
) {
    internal val positionX = Animatable(0f)

    public suspend fun shake(animationDuration: Int) {
        val shakeAnimationSpec: AnimationSpec<Float> = tween(
            durationMillis = animationDuration,
            easing = FastOutLinearInEasing,
        )

        when(direction) {
            Directions.LEFT -> shakeToLeft(shakeAnimationSpec)
            Directions.RIGHT -> shakeToRight(shakeAnimationSpec)
            Directions.LEFT_THEN_RIGHT -> shakeToLeftThenRight(shakeAnimationSpec)
            Directions.RIGHT_THEN_LEFT -> shakeToRightThenLeft(shakeAnimationSpec)
        }
    }

    private suspend fun shakeToLeft(animationSpec: AnimationSpec<Float>) {
        repeat(3) {
            positionX.animateTo(-strength.value, animationSpec)
            positionX.animateTo(0f, animationSpec)
        }
    }

    private suspend fun shakeToRight(animationSpec: AnimationSpec<Float>) {
        repeat(3) {
            positionX.animateTo(strength.value, animationSpec)
            positionX.animateTo(0f, animationSpec)
        }
    }

    private suspend fun shakeToLeftThenRight(animationSpec: AnimationSpec<Float>) {
        repeat(3) {
            positionX.animateTo(-strength.value, animationSpec)
            positionX.animateTo(strength.value / 2, animationSpec)
            positionX.animateTo(0f, animationSpec)
        }
    }

    private suspend fun shakeToRightThenLeft(animationSpec: AnimationSpec<Float>) {
        repeat(3) {
            positionX.animateTo(strength.value, animationSpec)
            positionX.animateTo(-strength.value / 2, animationSpec)
            positionX.animateTo(0f, animationSpec)
        }
    }

    public sealed class Strength(public val value: Float) {
        public data object Normal: Strength(15f)
        public data object Strong: Strength(30f)
        public data class Custom(val strength: Float) : Strength(strength)
    }

    public enum class Directions {
        LEFT,
        RIGHT,
        LEFT_THEN_RIGHT,
        RIGHT_THEN_LEFT,
    }
}

@Composable
public fun rememberShakingState(
    strength: ShakingState.Strength,
    direction: ShakingState.Directions,
) : ShakingState = remember { ShakingState(strength = strength, direction = direction) }

public fun Modifier.shakable(
    state: ShakingState,
): Modifier {
    return graphicsLayer(
        translationX = state.positionX.value
    )
}