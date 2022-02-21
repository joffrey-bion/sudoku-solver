package org.hildan.sudoku.solver.backtracking

import org.hildan.sudoku.checker.CheckResult
import org.hildan.sudoku.checker.check
import org.hildan.sudoku.model.Grid
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.useLines
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

@Execution(ExecutionMode.CONCURRENT)
class BacktrackingSolverTest {

    @ParameterizedTest
    @MethodSource("personalPuzzles")
    fun testPersonalPuzzles(puzzlesFile: Path) {
        testAllPuzzlesIn(puzzlesFile)
    }

    @ParameterizedTest
    @MethodSource("standardPuzzles")
    fun testStandardPuzzles(puzzlesFile: Path) {
        testAllPuzzlesIn(puzzlesFile)
    }

    private fun testAllPuzzlesIn(puzzlesFile: Path) {
        val avgTime = puzzlesFile.useLines { grids ->
            grids.map { encodedGrid ->
                measureTimeMillis { solveAndCheck(encodedGrid) }
            }.average().milliseconds
        }
        println("Average time $avgTime/grid for $puzzlesFile")
    }

    private fun solveAndCheck(encodedGrid: String) {
        val grid = Grid(encodedGrid)
        grid.solveWithBacktracking()
        assertEquals(CheckResult.Valid, grid.check())
    }

    companion object {

        @JvmStatic
        fun personalPuzzles(): List<Path> = Path("../dataset/personal").listDirectoryEntries()

        @JvmStatic
        fun standardPuzzles(): List<Path> = Path("../dataset/standard").listDirectoryEntries("puzzles*.txt")
    }
}
