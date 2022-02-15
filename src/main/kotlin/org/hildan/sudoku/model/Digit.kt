package org.hildan.sudoku.model

typealias Digit = Int

val ALL_DIGITS = (1..Grid.SIZE).toSet()

/**
 * Returns all possible combinations of [size] digits from this set.
 */
fun Set<Digit>.allTuplesOfSize(size: Int): Set<Set<Digit>> = when {
    size > this.size -> emptySet()
    size == 0 -> setOf(emptySet()) // one possibility: no digits
    size == this.size -> setOf(this) // one possibility: all digits from the set (shortcut here)
    else -> flatMapTo(HashSet()) { d ->
        (this - d).allTuplesOfSize(size - 1).map { smallerTuple -> smallerTuple + d }
    }
}
