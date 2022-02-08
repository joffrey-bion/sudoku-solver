package org.hildan.sudoku.drawing

/**
 * A utility class containing interesting characters and methods to help drawing stuff in a console.
 */
object Drawing {
    /**
     * Returns the concatenation of `nbSections` sections, each of them being the
     * concatenation of `sectionSize` times the String representation of `cBase`.
     * Between each section, the representation of`cStep` is inserted once.
     *
     * Typically, `cBase` and `cSep` are `char`s, [Character] s or [String]s.
     *
     * @param cBase The basic character to repeat in the sections.
     * @param nbSections The number of sections desired.
     * @param sectionSize The number of times `cBase` will be repeated in each section.
     * @param cSep The separator to put between each section.
     * @param prefix The left bound, inserted at the beginning of the result.
     * @param suffix The right bound, appended to the end of the result.
     *
     * @return A String of `nbSections` sections of `sectionSize` `cBase`, separated with `cSep`.
     */
    fun repeat(
        cBase: CharSequence,
        nbSections: Int,
        sectionSize: Int,
        cSep: CharSequence,
        prefix: CharSequence = "",
        suffix: CharSequence = "",
    ): String = (0 until nbSections).joinToString(separator = cSep, prefix = prefix, postfix = suffix) {
        cBase.repeat(sectionSize)
    }
}
