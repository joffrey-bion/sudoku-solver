package org.hildan.sudoku.solver

import org.hildan.sudoku.checker.CheckResult
import org.hildan.sudoku.checker.check
import org.hildan.sudoku.drawing.format
import org.hildan.sudoku.model.Grid
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Execution(ExecutionMode.CONCURRENT)
class HumanSolverTest {

    @Test
    fun wikiEasiest() = test("000105000140000670080002400063070010900000003010090520007200080026000035000409000")

    @Test
    fun wikiGentle() = test("000004028406000005100030600000301000087000140000709000002010003900000507670400000")

    @Test
    fun wikiModerate() = test("720096003000205000080004020000000060106503807040000000030800090000702000200430018")

    @Test
    fun wikiNakedTriples() = test("000000000001900500560310090100600028004000700270004003040068035002005900000000000")

    @Test
    fun wikiHiddenTriples() = test("300000000970010000600583000200000900500621003008000005000435002000090056000000001")

    // requires Naked Pairs, X-Wing
    @Test
    fun xWing() = test("600090007040007100002800050800000090000070000030000008050002300004500020900030004")

    // requires Pointing Pairs, Box/Line Reduction, Naked Quads, X-Wing
    @Test
    fun xWing2() = test("400090800000020050007500069000002000900040016035600700600000400010000000000000093")

    // requires Pointing Pairs, Box/Line Reduction, Swordfish, X-Wing
    @Test
    fun swordfish() = test("500060003003804000000100020026000400300020006009000350040005000000601700100070009")

    // requires Jellyfish, Naked Triple, Pointing Pairs
    @Test
    fun jellyfish() = test("020000030400000007001230400004150300005640100000000000002510600500000090080000005")

    private fun test(encodedGrid: String) {
        val grid = Grid(encodedGrid)
        val result = HumanSolver().solve(grid)
        try {
            assertEquals(CheckResult.Valid, grid.check(), "Invalid grid")
            assertTrue(grid.isComplete, "Couldn't solve the puzzle without backtracking")
        } finally {
            println(grid.format())
            println(result)
        }
    }
}
