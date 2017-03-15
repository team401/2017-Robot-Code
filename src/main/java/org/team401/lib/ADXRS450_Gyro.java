package org.team401.lib;

/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2015-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.hal.FRCNetComm;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Use a rate gyro to return the robots heading relative to a starting position. The Gyro class
 * tracks the robots heading based on the starting position. As the robot rotates the new heading is
 * computed by integrating the rate of rotation returned by the sensor. When the class is
 * instantiated, it does a short calibration routine where it samples the gyro while at rest to
 * determine the default offset. This is subtracted from each sample to determine the heading.
 *
 * This class is for the digital ADXRS450 gyro sensor that connects via SPI.
 */
@SuppressWarnings({"TypeName", "AbbreviationAsWordInName", "PMD.UnusedPrivateField"})
public class ADXRS450_Gyro extends GyroBase implements Gyro, PIDSource, LiveWindowSendable {
	private static final double kSamplePeriod = 0.001,
		kDegreePerSecondPerLSB = 0.0125;
	public static final double kCalibrationSampleTime = 5.0;

	private static final int kRateRegister = 0x00,
		kTemRegister = 0x02,
		kLoCSTRegister = 0x04,
		kHiCSTRegister = 0x06,
		kQuadRegister = 0x08,
		kFaultRegister = 0x0A,
		kPIDRegister = 0x0C,
		kSNHighRegister = 0x0E,
		kSNLowRegister = 0x10;

	private SPI m_spi;

	private boolean m_is_calibrating = false;
	private double m_last_center;

	/**
	 * Constructor.  Uses the onboard CS0.
	 */
	public ADXRS450_Gyro() {
		this(SPI.Port.kOnboardCS0);
	}

	/**
	 * Constructor.
	 *
	 * @param port The SPI port that the gyro is connected to
	 */
	public ADXRS450_Gyro(SPI.Port port) {
		m_spi = new SPI(port);
		m_spi.setClockRate(3000000);
		m_spi.setMSBFirst();
		m_spi.setSampleDataOnRising();
		m_spi.setClockActiveHigh();
		m_spi.setChipSelectActiveLow();

		// Validate the part ID
		if ((readRegister(kPIDRegister) & 0xff00) != 0x5200) {
			m_spi.free();
			m_spi = null;
			DriverStation.reportError("could not find ADXRS450 gyro on SPI port " + port.value, false);
			return;
		}

		m_spi.initAccumulator(kSamplePeriod, 0x20000000, 4, 0x0c00000e, 0x04000000,
				10, 16, true, true);

		HAL.report(FRCNetComm.tResourceType.kResourceType_ADXRS450, port.value);
		LiveWindow.addSensor("ADXRS450_Gyro", port.value, this);
	}

	@Override
	public synchronized void calibrate() {
		if (m_spi == null)
			return;

		startCalibrate();

		Timer.delay(kCalibrationSampleTime);

		endCalibrate();
	}

	public synchronized void startCalibrate() {
		if (m_spi == null)
			return;

		if (!m_is_calibrating) {
			m_is_calibrating = true;
			m_spi.setAccumulatorCenter(0);
			m_spi.resetAccumulator();
		}
	}

	public synchronized void endCalibrate() {
		if (m_is_calibrating) {
			m_is_calibrating = false;
			m_last_center = m_spi.getAccumulatorAverage();
			m_spi.setAccumulatorCenter((int) Math.round(m_last_center));
			m_spi.resetAccumulator();
		}
	}

	public synchronized void cancelCalibrate() {
		if (m_is_calibrating) {
			m_is_calibrating = false;
			m_spi.setAccumulatorCenter((int) Math.round(m_last_center));
			m_spi.resetAccumulator();
		}
	}

	public double getCenter() {
		return m_last_center;
	}

	private int readRegister(int reg) {
		int cmdhi = 0x8000 | (reg << 1);

		byte[] buffer = {
			(byte)(cmdhi >> 8),
			(byte)(cmdhi & 0xff),
			0,
			(byte)(cmdhi % 2 == 0 ? 0 : 1)};

		m_spi.write(buffer, 4);
		m_spi.read(false, buffer, 4);
		return (buffer[0] & 0xe0) == 0 ? 0 : (int)buffer[0] >> 5 & 0xffff;
	}


	//FIXED METHOD
	@Override
	public void reset() {
		if (m_spi != null)
			m_spi.resetAccumulator();
	}

	/**
	 * Delete (free) the spi port used for the gyro and onStop accumulating.
	 */
	@Override
	public void free() {
		if (m_spi != null) {
			m_spi.free();
			m_spi = null;
		}
	}

	@Override
	public double getAngle() {
		return m_spi == null ? 0.0 : m_spi.getAccumulatorValue() * kDegreePerSecondPerLSB * kSamplePeriod;
	}

	@Override
	public double getRate() {
		return m_spi == null ? 0.0 : m_spi.getAccumulatorLastValue() * kDegreePerSecondPerLSB;
	}
}