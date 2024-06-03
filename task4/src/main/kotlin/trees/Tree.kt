package trees

interface Tree<T : Comparable<T>> {
    suspend fun add(data: T)
    suspend fun contains(data: T): Boolean
    suspend fun delete(data: T)
}