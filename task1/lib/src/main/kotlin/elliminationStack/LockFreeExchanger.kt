package elliminationStack

import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicStampedReference
import kotlin.time.Duration
import kotlin.time.DurationUnit

class LockFreeExchanger<T> {
    companion object {
        const val EMPTY = 0
        const val WAITING = 1
        const val BUSY = 2
    }
    private val slot = AtomicStampedReference<T?>(null, EMPTY)
    fun exchange(myItem: T, timeout: Duration): T? {
        val timeBound = System.nanoTime() + timeout.toLong(DurationUnit.NANOSECONDS)
        val stampHolder = IntArray(1) { EMPTY }

        while (true) {
            if (System.nanoTime() > timeBound) throw TimeoutException()

            val yrItem = slot.get(stampHolder)
            when (stampHolder[0]) {
                EMPTY -> {
                    if (!slot.compareAndSet(yrItem, myItem, EMPTY, WAITING)) continue

                    while (System.nanoTime() < timeBound) {
                        val newtItem = slot.get(stampHolder)
                        if (stampHolder[0] == BUSY) {
                            slot.set(null, EMPTY)
                            return newtItem
                        }
                    }

                    if (slot.compareAndSet(myItem, null, WAITING, EMPTY))
                        throw TimeoutException()

                    val newtItem = slot.get(stampHolder)
                    slot.set(null, EMPTY)
                    return newtItem
                }

                WAITING -> if (slot.compareAndSet(yrItem, myItem, WAITING, BUSY))
                    return yrItem

                BUSY -> {}
            }
        }
    }
    }