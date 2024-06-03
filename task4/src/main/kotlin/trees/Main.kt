package trees

import trees.fineGrained.FineGrainedBinaryTree


suspend fun main() {
    val tree = FineGrainedBinaryTree<Int>()
    tree.add(10)
    tree.add(5)
    tree.add(15)
    tree.add(20)
    tree.add(1)
    println(tree.contains(20))
    println(tree.contains(10))
    tree.delete(1)
    tree.delete(10)
    println(tree.contains(10))

}