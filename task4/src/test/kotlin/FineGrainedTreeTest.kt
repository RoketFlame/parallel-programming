package trees

import org.junit.jupiter.api.BeforeEach
import trees.fineGrained.FineGrainedBinaryTree

class FineGrainedTreeTest : BinarySearchTreeTest() {
    @BeforeEach
    override fun setUp() {
        tree = FineGrainedBinaryTree()
    }
}