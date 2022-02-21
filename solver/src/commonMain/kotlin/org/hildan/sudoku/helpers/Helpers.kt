package org.hildan.sudoku.helpers

inline fun <T, K> Iterable<T>.groupIntoSetsBy(keySelector: (T) -> K): Map<K, Set<T>> {
    val result = HashMap<K, MutableSet<T>>()
    for (element in this) {
        val key = keySelector(element)
        val set = result.getOrPut(key) { HashSet() }
        set.add(element)
    }
    return result
}
