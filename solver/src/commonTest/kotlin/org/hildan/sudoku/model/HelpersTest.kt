package org.hildan.sudoku.model

import kotlin.test.Test
import kotlin.test.assertEquals

class HelpersTest {

    @Test
    fun allTuplesOfSize_tooBig_noPossibleTuple() {
        assertEquals(emptySet(), emptySet<Digit>().allTuplesOfSize(2))
        assertEquals(emptySet(), setOf(1).allTuplesOfSize(5))
        assertEquals(emptySet(), setOf(1, 2, 3).allTuplesOfSize(5))
    }

    @Test
    fun allTuplesOfSize_exactSize_oneTupleWithAllDigits() {
        assertEquals(setOf(emptySet()), emptySet<Digit>().allTuplesOfSize(0))
        assertEquals(setOf(setOf(1)), setOf(1).allTuplesOfSize(1))
        assertEquals(setOf(setOf(1, 2)), setOf(1, 2).allTuplesOfSize(2))
        assertEquals(setOf(setOf(1, 2, 3)), setOf(1, 2, 3).allTuplesOfSize(3))
    }

    @Test
    fun allTuplesOfSize_smallerSize_correctCombinations() {
        assertEquals(setOf(emptySet()), setOf(1).allTuplesOfSize(0))
        assertEquals(setOf(emptySet()), setOf(1, 2).allTuplesOfSize(0))
        assertEquals(setOf(setOf(1), setOf(2)), setOf(1, 2).allTuplesOfSize(1))
        assertEquals(setOf(setOf(1, 2), setOf(2, 3), setOf(1, 3)), setOf(1, 2, 3).allTuplesOfSize(2))
    }
}
