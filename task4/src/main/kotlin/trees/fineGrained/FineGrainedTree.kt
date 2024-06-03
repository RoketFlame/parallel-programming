package trees.fineGrained

import kotlinx.coroutines.sync.Mutex
import trees.BSNode
import trees.BSTree
import trees.Tree


class FineGrainedBinaryTree<T : Comparable<T>> : BSTree<T>() {

    private val mutex = Mutex()
    override var root: BSNode<T>? = null

    suspend fun helperContains(data: T): Pair<BSNode<T>?, BSNode<T>?> {
        suspend fun recContains(currentNode: BSNode<T>): Pair<BSNode<T>?, BSNode<T>?> {
            return if ((data == currentNode.data)) {
                currentNode.unlock()
                Pair(currentNode.parent, currentNode)
            } else if ((data < currentNode.data) and (currentNode.left != null)) {
                currentNode.left!!.lock()
                currentNode.unlock()
                recContains(currentNode.left!!)
            } else if ((data > currentNode.data) and (currentNode.right != null)) {
                currentNode.right!!.lock()
                currentNode.unlock()
                recContains(currentNode.right!!)
            } else {
                currentNode.unlock()
                Pair(currentNode, null)
            }
        }
        return if (root == null) {
            Pair(null, null)
        } else {
            recContains(root!!)
        }
    }

    override suspend fun contains(data: T): Boolean {
        if (root?.data == data) {
            return true
        }
        mutex.lock()
        root?.lock()
        return (helperContains(data).second?.data == data).also { mutex.unlock() }
    }

    override suspend fun get(data: T): T? {
        mutex.lock()
        root?.lock()
        return helperContains(data).second?.data.also { mutex.unlock() }
    }

    override suspend fun add(data: T) {
        mutex.lock()
        root?.lock()
        val parent = helperContains(data).first
        if (parent == null) {
            root = BSNode(data)
        } else {
            val node = BSNode(data)
            node.parent = parent
            if (parent.data < data) {
                parent.right = node
            } else {
                parent.left = node
            }
        }.also { mutex.unlock() }
    }

    override suspend fun delete(data: T) {
        suspend fun findRightSuccessor(node: BSNode<T>): BSNode<T> {
            node.right?.lock()
            if (node.right == null) {
                return node
            }
            var currentNode = node.right!!
            while (currentNode.right != null) {
                currentNode.right!!.lock()
                currentNode.unlock()
                currentNode = currentNode.right!!
            }
            return currentNode
        }

        fun replaceNode(nodeToReplace: BSNode<T>?, replacementNode: BSNode<T>?) {
            val parent = nodeToReplace!!.parent
            if (parent == null) {
                root = replacementNode
            } else {
                if (parent.left == nodeToReplace) {
                    parent.left = replacementNode
                } else {
                    parent.right = replacementNode
                }
            }
            replacementNode?.parent = parent
        }


        suspend fun deleteNode(node: BSNode<T>?) {
            if (node!!.left == null && node.right == null) {
                replaceNode(node, null)
            } else if (node.left == null || node.right == null) {
                replaceNode(node, if (node.left == null) node.right else node.left)
            } else {
                val successor = findRightSuccessor(node)
                node.data = successor.data
                deleteNode(successor)
            }
        }

        mutex.lock()
        if (root == null) {
            mutex.unlock()
        } else {
            root!!.lock()
            mutex.unlock()
            deleteNode(helperContains(data).second)
        }
    }
}

