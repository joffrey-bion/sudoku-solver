package org.hildan.sudoku.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.hildan.sudoku.solver.GridState
import org.hildan.sudoku.solver.SolvePath
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.max
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun StepViewer(path: SolvePath) {
    val (selectedStep, setSelectedStep) = remember { mutableStateOf(0) }
    val state = path.states[selectedStep]

    GridStateView(state)
    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
        }
    }) {
        Input(InputType.Button) {
            value("Prev step")
            if (selectedStep == 0) {
                disabled()
            }
            onClick {
                setSelectedStep(selectedStep - 1)
            }
        }
        Input(InputType.Range) {
            defaultValue(selectedStep)
            min("0")
            max(path.states.lastIndex.toString())
            onChange { event ->
                setSelectedStep(event.value?.toInt() ?: 0)
            }
        }
        Input(InputType.Button) {
            value("Next step")
            if (selectedStep == path.states.lastIndex) {
                disabled()
            }
            onClick {
                setSelectedStep(selectedStep + 1)
            }
        }
    }
}

@Composable
private fun GridStateView(state: GridState) {
    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
        }
    }) {
        SudokuGrid(state.grid)
        TechniqueInfo(state)
    }
}

@Composable
private fun TechniqueInfo(state: GridState) {
    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
        }
    }) {
        H2 {
            Text(state.step?.techniqueName ?: "Initial state")
        }
        P {
            Text("Some description here")
        }
    }
}
