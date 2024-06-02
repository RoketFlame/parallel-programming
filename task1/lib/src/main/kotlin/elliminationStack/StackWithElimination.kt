package elliminationStack

import stack.Node
import stack.Stack
import kotlinx.atomicfu.*
import java.util.concurrent.TimeoutException
import kotlin.time.Duration

class StackWithElimination<T>(private val capacity: Int, duration: Duration): Stack<T> {
    private val head = atomic<Node<T>?>(null)
    private val eliminationArray = EliminationArray<T>(capacity, duration)

    override fun push(value: T) {
        while (true) {
            val curHead = head.value
            val newHead = Node(value, curHead)
            if (head.compareAndSet(curHead, newHead)) {
                return
            }

            try {
                eliminationArray.visit(value, capacity) ?: return
            }
            catch (ex: TimeoutException) {
                continue
            }
        }
    }

    override fun pop(): T? {
        while (true) {
            val curHead = head.value
            if (head.compareAndSet(curHead, curHead?.next)) {
                return curHead?.value
            }

            try {
                val otherValue = eliminationArray.visit(null, capacity)
                if (otherValue != null) {
                    return otherValue
                }
            }
            catch (ex: TimeoutException) {
                continue
            }
        }
    }
    override fun top(): T? {
        return head.value?.value
    }
}