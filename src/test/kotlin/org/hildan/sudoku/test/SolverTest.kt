package org.hildan.sudoku.test

import org.hildan.sudoku.checker.CheckResult
import org.hildan.sudoku.checker.check
import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.solver.Solver
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.forEachLine
import kotlin.io.path.listDirectoryEntries
import kotlin.test.assertEquals

@Execution(ExecutionMode.CONCURRENT)
class SolverTest {

    @ParameterizedTest
    @MethodSource("standardPuzzles")
    fun testAllPuzzles(puzzlesFile: Path) {
        val solver = Solver()
        puzzlesFile.forEachLine { encodedGrid ->
            val grid = Grid(encodedGrid)
            solver.solve(grid)
            assertEquals(CheckResult.Valid, grid.check())
        }
    }

    companion object {

        @JvmStatic
        fun standardPuzzles(): List<Path> = Path("dataset/standard").listDirectoryEntries()
    }
}
