package trees

import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random
import kotlin.test.Test

const val NODE_COUNT = 1000

open class BinarySearchTreeTest {
    lateinit var tree: BSTree<Int>

    private var randomNodes = (0..NODE_COUNT).shuffled().take(NODE_COUNT)
    private val nodeCount = NODE_COUNT

    @BeforeEach
    open fun setUp() {
        tree = BSTree()
    }

    @Test
    fun nonParallel() {
        val randomNodes = (0..NODE_COUNT).shuffled().take(NODE_COUNT)
        runBlocking {
            repeat(NODE_COUNT) {
                tree.add(randomNodes[it])
            }
            for (key in randomNodes) {
                assertEquals(key, tree.get(key))
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun deleteTest() {
        var randomNodes = (0..NODE_COUNT).shuffled().take(NODE_COUNT)
        val jobs = mutableListOf<Job>()

        runBlocking {
            repeat(NODE_COUNT) {
                launch(newSingleThreadContext("Thread$it")) {
                    tree.add(randomNodes[it])
                }.let {
                    jobs.add(it)
                }
            }
            jobs.forEach {
                it.join()
            }
            randomNodes = randomNodes.shuffled(Random)
            repeat(NODE_COUNT) {
                launch(newSingleThreadContext("Thread$it")) {
                    tree.delete(randomNodes[it])
                }
            }
        }

        runBlocking {
            for (key in randomNodes) {
                assertEquals(null, tree.get(key))
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun addAndDelete() {
        runBlocking {
            coroutineScope {
                repeat(nodeCount) {
                    launch(newSingleThreadContext("Thread$it")) {
                        tree.add(randomNodes[it])
                    }
                }
            }
        }

        val nodesToDelete = randomNodes.shuffled(Random).take(nodeCount / 2)

        runBlocking {
            coroutineScope {
                repeat(nodeCount / 2) {
                    launch(newSingleThreadContext("Thread$it")) {
                        tree.delete(nodesToDelete[it])
                    }
                }
            }
        }
        runBlocking {
            for (key in randomNodes) {
                if (key !in nodesToDelete) {
                    assertEquals(key, tree.get(key))
                } else {
                    assertEquals(null, tree.get(key))
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun addAndGet() {
        runBlocking {
            coroutineScope {
                repeat(nodeCount) {
                    launch(newSingleThreadContext("Thread$it")) {
                        tree.add(randomNodes[it])
                    }
                }
            }
            for (i in randomNodes) {
                assertEquals(i, tree.get(i))
            }
        }
    }
}