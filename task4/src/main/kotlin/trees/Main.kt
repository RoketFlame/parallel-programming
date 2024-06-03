package trees

import trees.fineGrained.FineGrainedBinaryTree


suspend fun main() {
    val tree = FineGrainedBinaryTree<Int>()
    tree.add(10)
    tree.add(5)
    tree.add(15)
    tree.add(20)
    tree.add(1)
//    println(tree.preOrder())
    tree.delete(1)
    println(tree.preOrder())
    tree.delete(10)
    println(tree.preOrder())
//    tree.delete(2)
//    println(tree.preOrder())
    tree.delete(15)
    println(tree.preOrder())
    tree.delete(5)
    println(tree.preOrder())
    tree.delete(20)
    println(tree.preOrder())

}