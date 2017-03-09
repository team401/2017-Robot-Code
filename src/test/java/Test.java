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
        for (int i = 0; i <= 90; i++) {
            Rotation2d a = Rotation2d.fromDegrees(i);
            Rotation2d b = Rotation2d.fromDegrees(90-i);
            System.out.println(a + " " + b + " " + a.inverse().rotateBy(b));
        }
    }
}
