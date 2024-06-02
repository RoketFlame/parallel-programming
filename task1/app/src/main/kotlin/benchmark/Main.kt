package benchmark

import elliminationStack.StackWithElimination
import kotlinx.coroutines.*
import stack.Stack
import stack.TreiberStack
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

const val RAND_INT = 1000
fun executeCurrentMethod(stack: Stack<Int>, condition: Int) {
    when (condition) {
        0 -> stack.push(Random.nextInt(RAND_INT))
        1 -> stack.pop()
        else -> stack.top()
    }
}

fun fillStack(stack: Stack<Int>, iterations: Int) {
    repeat(iterations) {
        stack.push(Random.nextInt(RAND_INT))
    }
}

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
fun runTest(stack: Stack<Int>, threads: Int, iterations: Int, efficient: Boolean): Long {
    val jobs = mutableListOf<Job>()
    return measureTimeMillis {
        runBlocking {
            repeat(threads) { curThread ->
                jobs.add(launch(newSingleThreadContext(curThread.toString())) {
                    repeat(iterations) {
                        if (efficient) {
                            executeCurrentMethod(stack, (curThread % 2))
                        } else {
                            executeCurrentMethod(stack, (curThread + Random.nextInt(3)) % 3)
                        }
                    }
                })
            }
            jobs.joinAll()
        }
    }
}
fun Double.format(digits: Int) = "%.${digits}f".format(this)

const val REPEATS = 50
const val EFFICIENT = true
fun main() {
    val iterations = 1_000_000
    val threads = mutableListOf(1, 2, 4, 6, 12)
    for (thread in threads) {
        var eliminationTime = 0L
        var stackTime = 0L
        repeat(REPEATS) {
            val stack = TreiberStack<Int>()
            fillStack(stack, 5_000)
            val eliminationStack = StackWithElimination<Int>(10, (50).toDuration(DurationUnit.NANOSECONDS))
            eliminationTime += runTest(eliminationStack, thread, iterations, EFFICIENT)
            stackTime += runTest(stack, thread, iterations, EFFICIENT)
        }
        eliminationTime /= REPEATS
        stackTime /= REPEATS

        println("$thread \t $eliminationTime \t $stackTime \t ${(stackTime.toDouble() / eliminationTime).format(2)}")
    }
}