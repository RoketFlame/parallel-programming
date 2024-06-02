import elliminationStack.StackWithElimination
import org.jetbrains.kotlinx.lincheck.LoggingLevel
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Suppress("UNUSED")
class TreiberStackEliminationTest {

    private val stack = StackWithElimination<Int>(10, (50).toDuration(DurationUnit.MICROSECONDS))

    @Operation
    fun push(value: Int) = stack.push(value)

    @Operation
    fun pop() = stack.pop()

    @Operation
    fun top() = stack.top()

    @Test
    fun stressTest() =
        StressOptions()
            .iterations(50)
            .invocationsPerIteration(50_000)
            .threads(3)
            .actorsPerThread(3)
            .sequentialSpecification(SequentialStackInt::class.java)
            .logLevel(LoggingLevel.INFO)
            .check(this::class.java)
}