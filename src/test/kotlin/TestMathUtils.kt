import junit.framework.TestCase
import org.team401.robot.MathUtils

class TestMathUtils : TestCase() {

    fun testNormalize() {
        val speeds = doubleArrayOf(0.7, 1.2, 0.95)
        MathUtils.normalize(speeds)

        assertEquals(0.7/1.2, speeds[0])
        assertEquals(1.2/1.2, speeds[1])
        assertEquals(0.95/1.2, speeds[2])
    }

    fun testScale() {
        val speeds = doubleArrayOf(0.7, 0.8, 0.9)
        MathUtils.scale(speeds, 1.5)

        assertEquals(0.7*1.5, speeds[0])
        assertEquals(0.8*1.5, speeds[1])
        assertEquals(0.9*1.5, speeds[2])
    }

    fun testRotateVector() {

    }

    fun testToRange() {
        val min = 0.0
        val max = 10.0
        val newMin = 100.0
        val newMax = 200.0

        var newX: Double

        newX = MathUtils.toRange(0.0, min, max, newMin, newMax)
        assertEquals(100.0, newX)

        newX = MathUtils.toRange(5.0, min, max, newMin, newMax)
        assertEquals(150.0, newX)

        newX = MathUtils.toRange(10.0, min, max, newMin, newMax)
        assertEquals(200.0, newX)
    }

}