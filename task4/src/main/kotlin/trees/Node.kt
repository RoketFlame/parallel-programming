package trees

import kotlinx.coroutines.sync.Mutex

interface Node<T : Comparable<T>, Subtype : Node<T, Subtype>> : Comparable<Node<T, Subtype>> {
    val data: T
    val left: Subtype?
    val right: Subtype?
    val parent: Subtype?
}

abstract class AbstractNode<T : Comparable<T>, Subtype : AbstractNode<T, Subtype>> : Node<T, Subtype> {
    //stores the data value of the current node
    abstract override var data: T
        internal set

    //stores the left subtree of the current node
    abstract override var left: Subtype?
        internal set

    //stores the right subtree of the current node
    abstract override var right: Subtype?
        internal set

    //stores the parent node of the current node
    abstract override var parent: Subtype?
        internal set

    //method to compare nodes based on their data values
    override fun compareTo(other: Node<T, Subtype>): Int {
        return data.compareTo(other.data)
    }

    //method to get the hash code of the current node
    override fun hashCode(): Int {
        return data.hashCode()
    }

    //method for comparing the current node with another object,
    // checking for equality of the value of the data property
    override fun equals(other: Any?): Boolean {
        if (other is AbstractNode<*, *>) {
            return data == other.data
        }
        return false
    }

    //returns a string representation of the node in data format.
    override fun toString(): String {
        return "$data"
    }
}

class BSNode<T : Comparable<T>>(
    //stores the data value of the current node
    override var data: T,
    //stores the left subtree of the current node
    override var left: BSNode<T>? = null,
    //stores the right subtree of the current node
    override var right: BSNode<T>? = null,
    //stores the parent node of the current node
    override var parent: BSNode<T>? = null,

    ) : AbstractNode<T, BSNode<T>>() {
    private val mutex = Mutex()
    suspend fun lock() = mutex.lock()
    fun unlock() = mutex.unlock()
}