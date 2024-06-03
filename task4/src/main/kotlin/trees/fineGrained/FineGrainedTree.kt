package trees.fineGrained

import kotlinx.coroutines.sync.Mutex
import trees.BSNode
import trees.BSTree


class FineGrainedBinaryTree<T : Comparable<T>> : BSTree<T>() {

    private val mutex = Mutex()
    override var root: BSNode<T>? = null

    private suspend fun recContains(data: T, currentNode: BSNode<T>): Pair<BSNode<T>?, BSNode<T>?> {
        return if ((data == currentNode.data)) {
            currentNode.unlock()
            Pair(currentNode.parent, currentNode)
        } else if ((data < currentNode.data) and (currentNode.left != null)) {
            currentNode.left!!.lock()
            currentNode.unlock()
            recContains(data, currentNode.left!!)
        } else if ((data > currentNode.data) and (currentNode.right != null)) {
            currentNode.right!!.lock()
            currentNode.unlock()
            recContains(data, currentNode.right!!)
        } else {
            currentNode.unlock()
            Pair(currentNode, null)
        }
    }

    suspend fun helperContains(data: T): Pair<BSNode<T>?, BSNode<T>?> {
        mutex.lock()
        return if (root == null) {
            mutex.unlock()
            Pair(null, null)
        } else {
            root!!.lock()
            mutex.unlock()
            recContains(data, root!!)
        }
    }

    override suspend fun contains(data: T): Boolean {
        if (root?.data == data) {
            return true
        }
        return (helperContains(data).second?.data == data)
    }

    override suspend fun get(data: T): T? {
        return helperContains(data).second?.data
    }

    override suspend fun add(data: T) {
        mutex.lock()
        if (root == null) {
            root = BSNode(data)
            mutex.unlock()
            return
        }
        root!!.lock()
        mutex.unlock()
        val (parent, cur) = recContains(data, root!!)
        if (parent != null) {
            val node = BSNode(data)
            if (parent.data > data) {
                parent.left = node
                node.parent = parent
            } else {
                parent.right = node
                node.parent = parent
            }
        }
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


        suspend fun deleteNode(node: BSNode<T>) {
            if (node.left == null && node.right == null) {
                replaceNode(node, null)
            } else if (node.left == null || node.right == null) {
                replaceNode(node, if (node.left == null) node.right else node.left)
            } else {
                val successor = findRightSuccessor(node.left!!)
                node.data = successor.data
                deleteNode(successor)
            }
        }

        mutex.lock()
        if (root?.data == data) {
            deleteNode(root!!)
            mutex.unlock()
            return
        }
        mutex.unlock()
        root?.lock()
        val node = recContains(data, root!!).second ?: return
        if (node == root) {
            deleteNode(node)
        }
        deleteNode(node)
    }
}

