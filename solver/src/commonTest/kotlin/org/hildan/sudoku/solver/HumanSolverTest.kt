package org.hildan.sudoku.solver

import org.hildan.sudoku.checker.CheckResult
import org.hildan.sudoku.checker.check
import org.hildan.sudoku.drawing.format
import org.hildan.sudoku.model.Grid
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HumanSolverTest {

    @Test
    fun wiki_easiest() = test("000105000140000670080002400063070010900000003010090520007200080026000035000409000")

    @Test
    fun wiki_gentle() = test("000004028406000005100030600000301000087000140000709000002010003900000507670400000")

    @Test
    fun wiki_moderate() = test("720096003000205000080004020000000060106503807040000000030800090000702000200430018")

    @Test
    fun wiki_nakedTriples() = test("000000000001900500560310090100600028004000700270004003040068035002005900000000000")

    @Test
    fun wiki_hiddenTriples() = test("300000000970010000600583000200000900500621003008000005000435002000090056000000001")

    // requires Naked Pairs, X-Wing
    @Test
    fun xWing_easy() = test("600090007040007100002800050800000090000070000030000008050002300004500020900030004")

    // requires Pointing Pairs, Box/Line Reduction, Naked Quads, X-Wing
    @Test
    fun xWing_hard() = test("400090800000020050007500069000002000900040016035600700600000400010000000000000093")

    // requires Pointing Pairs, Box/Line Reduction, Swordfish, X-Wing
    @Test
    fun swordfish() = test("500060003003804000000100020026000400300020006009000350040005000000601700100070009")

    // requires Jellyfish, Naked Triple, Pointing Pairs
    @Test
    fun jellyfish() = test("020000030400000007001230400004150300005640100000000000002510600500000090080000005")

    // requires Naked Pairs, Simple Coloring on 2s
    @Ignore
    @Test
    fun simpleColoring() = test("179850000632497851854000709293500467581746392467329518706905100918000005305100900")

    // requires Naked Tuples, X-Wing and Y-Wing
    @Ignore
    @Test
    fun wiki_tough() = test("309000400200709000087000000750060230600904008028050041000000590000106007006000104")

    // requires X-Wing, Y-Wing, Simple Coloring, Naked Triples
    @Ignore
    @Test
    fun wiki_xWing() = test("093004560060003140004608309981345000347286951652070483406002890000400010029800034")

    // requires Pointing pairs, Swordfish, XY-Chain
    @Ignore
    @Test
    fun wiki_swordfish() = test("050030602642895317037020800023504700406000520571962483214000900760109234300240170")

    // requires Naked Triples, Box/Line Reduction, Jellyfish, Y-Wing
    @Ignore
    @Test
    fun wiki_jellyfish() = test("024090008800402900719000240075804300240900587038507604082000059007209003490050000")

    // requires WXYZ Wing, Alternating Inference Chains, Swordfish, Simple Coloring, XY-Chain
    @Ignore
    @Test
    fun wiki_emptyRectangle() = test("000602400002403600000080021070000840309040207045000030500060000003159780008204500")

    private fun test(encodedGrid: String) {
        val grid = Grid(encodedGrid)
        val result = HumanSolver().solve(grid)
        try {
            assertEquals(CheckResult.Valid, grid.check(), "Invalid grid")
            assertTrue(grid.isComplete, "Couldn't solve the puzzle without backtracking")
        } catch(e: Throwable) {
            println(grid.format())
            println(result)
            throw e
        }
    }
}
