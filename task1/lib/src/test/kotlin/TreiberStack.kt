
import org.jetbrains.kotlinx.lincheck.LoggingLevel
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.junit.jupiter.api.Test
import stack.Stack
import stack.TreiberStack

class SequentialStackInt : Stack<Int> {
    private val stack = ArrayDeque<Int>()

    override fun top() = stack.firstOrNull()
    override fun pop() = stack.removeFirstOrNull()
    override fun push(value: Int) = stack.addFirst(value)
}
@Suppress("UNUSED")
class TreiberStackTest {

    private val stack = TreiberStack<Int>()

    @Operation
    fun push(value: Int) = stack.push(value)

    @Operation
    fun pop() = stack.pop()

    @Operation
    fun top() = stack.top()

    @Test
    fun stressTest() =
        ModelCheckingOptions()
            .iterations(50)
            .invocationsPerIteration(50_000)
            .threads(3)
            .actorsPerThread(3)
            .sequentialSpecification(SequentialStackInt::class.java)
            .logLevel(LoggingLevel.INFO)
            .checkObstructionFreedom(true)
            .check(this::class)
}