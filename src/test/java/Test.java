import com.ctre.CANTalon;
import junit.framework.TestCase;
import org.team401.lib.MotionProfile;
import org.team401.lib.MotionProfileParser;
import org.team401.lib.Rotation2d;

public class Test extends TestCase {

    public void test() {
        MotionProfile p = MotionProfileParser.INSTANCE.parse("test", "/Volumes/Macintosh HD/Users/zach/Desktop/Lefts.csv");
        CANTalon.TrajectoryPoint point;
        do {
            point = p.getNextTrajectoryPoint();
            System.out.println(point.position + "," + point.velocity + "," + point.timeDurMs);
        } while (!point.isLastPoint);
    }

    public void testAngles() {
        Rotation2d a = Rotation2d.Companion.fromDegrees(50), b;
        for (int i = 0; i <= 360; i+=45) {
            b = Rotation2d.Companion.fromDegrees(i);
            System.out.println(a + " " + b + " " + a.inverse().rotateBy(b));
        }
    }
}
