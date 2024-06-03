package trees

import org.junit.jupiter.api.BeforeEach

class CoarseGrainedTreeTests : BinarySearchTreeTest() {
    @BeforeEach
    override fun setUp() {
        tree = CoarseGrainedTree()
    }
}