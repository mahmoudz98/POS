package com.casecode.pos.core.designsystem.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.casecode.pos.core.designsystem.theme.POSTheme

@Composable
fun Stepper(
    modifier: Modifier = Modifier,
    numberOfSteps: Int,
    currentStep: Int,
    stepDescriptionList: List<String> = List(numberOfSteps) { "" },
    unSelectedColor: Color = Color.LightGray,
    selectedColor: Color,
    onSelectedColor: Color,
) {
    val descriptionList = MutableList(numberOfSteps) { "" }

    stepDescriptionList.forEachIndexed { index, element ->
        if (index < numberOfSteps) {
            descriptionList[index] = element
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (step in 1..numberOfSteps) {
            Step(
                modifier = Modifier.weight(1F),
                step = step,
                isCompete = step < currentStep,
                isCurrent = step == currentStep,
                isComplete = step == numberOfSteps,
                stepDescription = descriptionList[step - 1],
                unSelectedColor = unSelectedColor,
                selectedColor = selectedColor,
                onSelectedColor = onSelectedColor,
            )
        }
    }
}

@Composable
private fun Step(
    modifier: Modifier = Modifier,
    step: Int,
    isCompete: Boolean,
    isCurrent: Boolean,
    isComplete: Boolean,
    stepDescription: String,
    selectedColor: Color,
    onSelectedColor: Color,
    unSelectedColor: Color,
) {
    val transition = updateTransition(isCompete, label = "")

    val innerCircleColor by transition.animateColor(label = "innerCircleColor") {
        if (it) selectedColor else unSelectedColor
    }
    val textColor by transition.animateColor(label = "txtColor") { if (it || isCurrent) selectedColor else unSelectedColor }

    val color by transition.animateColor(label = "color") { if (it || isCurrent) selectedColor else Color.Gray }

    val borderStroke = BorderStroke(1.dp, color)

    val textSize by remember { mutableStateOf(12.sp) }

    ConstraintLayout(modifier = modifier) {
        val (circle, txt, line) = createRefs()

        Surface(
            shape = CircleShape,
            border = borderStroke,
            color = innerCircleColor,
            modifier =
                Modifier
                    .size(30.dp)
                    .constrainAs(circle) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    },
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompete) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        "done",
                        modifier = modifier.padding(4.dp),
                        tint = onSelectedColor,
                    )
                } else {
                    Text(
                        text = step.toString(),
                        color = color,
                        fontSize = 10.sp,
                    )
                }
            }
        }

        Text(
            modifier =
            Modifier.constrainAs(txt) {
                top.linkTo(circle.bottom, margin = 3.dp)
                start.linkTo(circle.start)
                end.linkTo(circle.end)
                bottom.linkTo(parent.bottom)
            },
            fontSize = textSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = stepDescription,
            color = textColor,
        )

        if (!isComplete) {
            HorizontalDivider(
                modifier =
                Modifier.constrainAs(line) {
                    top.linkTo(circle.top)
                    bottom.linkTo(circle.bottom)
                    start.linkTo(circle.end)
                },
                color = innerCircleColor,
                thickness = 1.dp,
            )
        }
    }
}

@Preview
@Composable
fun StepperPreview() {
    POSTheme {
        Surface(
            modifier = Modifier.size(300.dp),
            color = MaterialTheme.colorScheme.background,
        ) {
            Stepper(
                modifier = Modifier.size(300.dp),
                numberOfSteps = 4,
                currentStep = 2,
                stepDescriptionList =
                listOf(
                    "Business info",
                    "Branches",
                    "Subscription",
                    "Employees",
                ),
                selectedColor = MaterialTheme.colorScheme.primary,
                onSelectedColor = MaterialTheme.colorScheme.onPrimary,
                unSelectedColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}