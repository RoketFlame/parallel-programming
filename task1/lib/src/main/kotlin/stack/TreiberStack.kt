package stack

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop


class TreiberStack<T> : Stack<T> {
    private val head = atomic<Node<T>?>(null)

    override fun push(value: T) {
        head.loop { curHead ->
            val newHead = Node(value, curHead)
            if (head.compareAndSet(curHead, newHead)) {
                return
            }
        }
    }

    override fun pop(): T? {
        head.loop { curHead ->
            if (head.compareAndSet(curHead, curHead?.next)) {
                return curHead?.value
            }
        }
    }

    override fun top(): T? = head.value?.value
}