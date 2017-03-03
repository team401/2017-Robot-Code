import com.ctre.CANTalon;
import junit.framework.TestCase;
import org.team401.lib.MotionProfile;
import org.team401.lib.MotionProfileParser;

public class Test extends TestCase {

    public void test() {
        MotionProfile p = MotionProfileParser.INSTANCE.parse("test", "/Volumes/Macintosh HD/Users/zach/Desktop/Lefts.csv");
        CANTalon.TrajectoryPoint point;
        do {
            point = p.getNextTrajectoryPoint();
            System.out.println(point.position + "," + point.velocity + "," + point.timeDurMs);
        } while (!point.isLastPoint);
    }
}
