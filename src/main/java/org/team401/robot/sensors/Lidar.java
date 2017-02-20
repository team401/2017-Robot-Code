package org.team401.robot.sensors;

import edu.wpi.first.wpilibj.I2C;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Lidar implements DistanceSensor {

    public enum Hardware {
        //           ADDR  RGST  VRD   VRST  R2BR
        LIDARLITE_V3(0x62, 0x00, 0x04, 0x00, 0x8F);

        public final int address;
        public final int mainRegister;
        public final int vRead;
        public final int vReset;
        public final int r2ByteRead;

        Hardware(int address, int mainRegister, int vRead, int vReset, int r2ByteRead) {
            this.address = address;
            this.mainRegister = mainRegister;
            this.vRead = vRead;
            this.vReset = vReset;
            this.r2ByteRead = r2ByteRead;
        }
    }

    public enum Unit {
        CENTIMETERS(1),
        METERS(0.01),
        INCHES(0.39370),
        FEET(0.032808);

        public final double multiplier;

        Unit(double multiplier) {
            this.multiplier = multiplier;
        }
    }

    private class PollTask implements Runnable {
        private I2C bus;
        private Hardware hardware;
        private Unit unit;
        private AtomicReference<Data> latestData;
        private byte[] buffer = {0,0};
        private byte[] writeBuffer = new byte[1];

        private PollTask(I2C bus, Hardware hardware, Unit unit) {
            this.bus = bus;
            this.hardware = hardware;
            this.unit = unit;
            latestData = new AtomicReference<>(new Data(0, Unit.CENTIMETERS));
            writeBuffer[0] = (byte) this.hardware.r2ByteRead;
        }

        @Override
        public void run() {
            bus.write(0x00, 0x04); //Tell the Lidar to read
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {}
            bus.writeBulk(writeBuffer);
            bus.readOnly(buffer, 2); //Read the bytes
            latestData.set(new Data(((buffer[0] << 8) + buffer[1]), unit)); //Write the data to the data holder
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }
    }

    public class Data {
        private Unit unit;
        private double nativeUnit;
        private Data(double nativeUnit, Unit unit) {
            this.unit = unit;
            this.nativeUnit = nativeUnit;
        }

        public double getDistance(Unit unit) {
            return nativeUnit * unit.multiplier;
        }

        public double getDistance() {
            return this.getDistance(unit);
        }
    }



    private I2C bus; //The bus to communicate on
    private Hardware hardware; //The hardware to use
    private Unit unit; //The unit to use
    private ScheduledThreadPoolExecutor executor;
    private PollTask task;
    private ScheduledFuture<?> future;

    public Lidar(I2C.Port port, Hardware hardware, Unit unit) {
        bus = new I2C(port, hardware.address);
        this.hardware = hardware;
        this.unit = unit;
        task = new PollTask(this.bus, this.hardware, this.unit); //Initialize the task to be run
        executor = new ScheduledThreadPoolExecutor(1); //We want one task to be run at a time
        bus.write(0x00, 0x00); //Reset the Lidar
    }

    public Lidar(I2C.Port port, Hardware hardware) {
        this(port, hardware, Unit.INCHES);
    }

    public void start(int period) {
        future = executor.scheduleAtFixedRate(task, 0, period, TimeUnit.MILLISECONDS);
    }

    public void start() {
        this.start(20);
    }

    public void stop() {
        if (future != null) {
            future.cancel(false); //Stop at the next available break
        }
    }

    public Data getLatestData() {
        return task.latestData.get();
    }

    @Override
    public double getDistance() {
        return getLatestData().getDistance();
    }
}