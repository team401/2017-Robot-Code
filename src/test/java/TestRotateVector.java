import org.junit.Test;
import org.team401.robot.math.MathUtils;

import java.text.DecimalFormat;

public class TestRotateVector {

    @Test
    public void testRotateVector() {
        System.out.println("InputX\tInputY\tGyroAngle\tOutputX\tOutputY");
        for (int i = -15; i <= 15; i++) {
            DecimalFormat df = new DecimalFormat("0.000");
            double[] speeds = MathUtils.INSTANCE.rotateVector(1, 0, i);
            String inputX = df.format(1);
            String inputY = df.format(0);
            String gyroAngle = df.format(i);
            String outputX = df.format(speeds[0]);
            String outputY = df.format(speeds[1]);

            System.out.println(inputX + "\t" + inputY + "\t" + gyroAngle + "\t\t" + outputX + "\t" + outputY);
        }
    }
}
