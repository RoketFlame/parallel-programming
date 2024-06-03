package trees

open class BSTree<T : Comparable<T>> : Tree<T> {
    internal open var root: BSNode<T>? = null

    //This function is called after a node has been inserted or removed from the tree,
    // and is used to balance the tree if necessary.
    // The default implementation simply returns the starting node without performing any balancing.

    override suspend fun add(data: T) {
        root = internalAdd(root, BSNode(data))
    }

    private fun internalAdd(initNode: BSNode<T>?, node: BSNode<T>): BSNode<T> {

        if (initNode == null) {
            return node
        }

        if (initNode < node) {
            initNode.right = internalAdd(initNode.right, node)
            initNode.right?.parent = initNode
        } else if (initNode > node) {
            initNode.left = internalAdd(initNode.left, node)
            initNode.left?.parent = initNode
        }
        return initNode
    }

    //This function is used to remove a node from a tree while ensuring that the tree remains balanced.
    // It recursively traverses the tree until it finds the node to be removed, and then calls

    override suspend fun delete(data: T) {
        root = internalDelete(root, BSNode(data))
        root?.parent = null
    }

    private fun internalDelete(initNode: BSNode<T>?, node: BSNode<T>): BSNode<T>? {
        if (initNode == null) {
            return null
        }
        if (initNode < node) {
            initNode.right = internalDelete(initNode.right, node)
            initNode.right?.parent = initNode
        } else if (initNode > node) {
            initNode.left = internalDelete(initNode.left, node)
            initNode.left?.parent = initNode
        } else {
            if ((initNode.left == null) || (initNode.right == null)) {
                return initNode.left ?: initNode.right
            } else {
                initNode.right?.let {
                    val tmp = getMinimal(it)
                    initNode.data = tmp.data
                    initNode.right = internalDelete(initNode.right, tmp)
                    initNode.right?.parent = initNode
                }
            }
        }
        return initNode
    }

    open suspend fun get(data: T): T? {
        return internalContains(root, BSNode(data))?.data
    }

    override suspend fun contains(data: T): Boolean {
        return (internalContains(root, BSNode(data)) != null)
    }

    //This function is used to check if a node matches with the same value as
    private fun internalContains(initNode: BSNode<T>?, node: BSNode<T>): BSNode<T>? {
        if (initNode == null) {
            return null
        }

        return if (initNode < node) {
            internalContains(initNode.right, node)
        } else if (initNode > node) {
            internalContains(initNode.left, node)
        } else {
            initNode
        }
    }

    //This function returns the node with the smallest value in the subtree rooted at the specified node.
    private fun getMinimal(node: BSNode<T>): BSNode<T> {
        var minNode = node
        while (true) {
            minNode = minNode.left ?: break
        }
        return minNode
    }


    //This function returns a preview of the tree as a list of nodes.
    // It uses the walk internal function to recursively traverse the tree in advance
    // and add each node to the list.
    open suspend fun preOrder(): List<BSNode<T>> {
        val result = mutableListOf<BSNode<T>>()
        fun walk(node: BSNode<T>, lst: MutableList<BSNode<T>>) {
            lst.add(node)
            node.left?.let { walk(it, lst) }
            node.right?.let { walk(it, lst) }
        }
        if (root == null) return result
        root?.let { walk(it, result) }
        return result
    }
}