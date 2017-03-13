package org.team401.lib;

import edu.wpi.first.wpilibj.util.BoundaryException;

/**
 * This class implements a PID Control Loop.
 * 
 * Does all computation synchronously (i.e. the calculate() function must be
 * called by the user from his own thread)
 */
public class SynchronousPID {

	private double kP, // factor for "proportional" control
		kI, // factor for "integral" control
		kD, // factor for "derivative" control
		maximumOutput = 1.0, // |maximum output|
		minimumOutput = -1.0, // |minimum output|
		maximumInput = 0.0, // maximum input - limit setpoint to this
		minimumInput = 0.0, // minimum input - limit setpoint to this
		prevError = 0.0, // the prior sensor input (used to compute velocity)
		totalError = 0.0, // the sum of the errors for use in the integral calc
		setpoint = 0.0,
		error = 0.0,
		result = 0.0,
		lastInput = Double.NaN,
		deadband = 0.0; // If the absolute error is less than deadband, treat error for proportional term as 0

	private boolean continuous = false; // do the endpoints wrap around? eg. Absolute encoder

	public SynchronousPID() {}

	/**
	 * Allocate a PID object with the given constants for P, I, D
	 *
	 * @param kP
	 *            the proportional coefficient
	 * @param kI
	 *            the integral coefficient
	 * @param kD
	 *            the derivative coefficient
	 */
	public SynchronousPID(double kP, double kI, double kD) {
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
	}

	/**
	 * Read the input, calculate the output accordingly, and write to the
	 * output. This should be called at a constant rate by the user (ex. in a
	 * timed thread)
	 *
	 * @param input
	 *            the input
	 */
	public double calculate(double input) {
		lastInput = input;
		error = setpoint - input;
		if (continuous)
			if (Math.abs(error) > (maximumInput - minimumInput) / 2)
				error += (maximumInput - minimumInput) * error > 0 ? -1 : 1;

		totalError += error * kP < maximumOutput && error * kP > minimumOutput ? error : 0;

		// Don't blow away m_error so as to not break derivative
		double proportionalError = Math.abs(error) < deadband ? 0 : error;

		result = (kP * proportionalError + kI * totalError + kD * (error - prevError));
		prevError = error;

		if (result > maximumOutput)
			result = maximumOutput;
		else if (result < minimumOutput)
			result = minimumOutput;

		return result;
	}

	/**
	 * Set the PID controller gain parameters. Set the proportional, integral,
	 * and differential coefficients.
	 *
	 * @param p
	 *            Proportional coefficient
	 * @param i
	 *            Integral coefficient
	 * @param d
	 *            Differential coefficient
	 */
	public void setPID(double p, double i, double d) {
		kP = p;
		kI = i;
		kD = d;
	}

	/**
	 * Getters for P, I, D, and current PID result.
     */
	public double getP() {
		return kP;
	}
	public double getI() {
		return kI;
	}
	public double getD() {
		return kD;
	}
	public double getResult() {
		return result;
	}

	/**
	 * Set the PID controller to consider the input to be continuous, Rather
	 * then using the max and min in as constraints, it considers them to be the
	 * same point and automatically calculates the shortest route to the
	 * setpoint.
	 *
	 * @param continuous
	 *            Set to true turns on continuous, false turns off continuous
	 */
	public void setContinuous(boolean continuous) {
		this.continuous = continuous;
	}

	public void setDeadband(double deadband) {
		this.deadband = deadband;
	}

	/**
	 * Set the PID controller to consider the input to be continuous, Rather
	 * then using the max and min in as constraints, it considers them to be the
	 * same point and automatically calculates the shortest route to the
	 * setpoint.
	 */
	public void setContinuous() {
		this.setContinuous(true);
	}

	/**
	 * Sets the maximum and minimum values expected from the input.
	 *
	 * @param minimumInput
	 *            the minimum value expected from the input
	 * @param maximumInput
	 *            the maximum value expected from the output
	 */
	public void setInputRange(double minimumInput, double maximumInput) {
		if (minimumInput > maximumInput)
			throw new BoundaryException("Lower bound is greater than upper bound");

		this.minimumInput = minimumInput;
		this.maximumInput = maximumInput;
		setSetpoint(setpoint);
	}

	/**
	 * Sets the minimum and maximum values to write.
	 *
	 * @param minimumOutput
	 *            the minimum value to write to the output
	 * @param maximumOutput
	 *            the maximum value to write to the output
	 */
	public void setOutputRange(double minimumOutput, double maximumOutput) {
		if (minimumOutput > maximumOutput)
			throw new BoundaryException("Lower bound is greater than upper bound");

		this.minimumOutput = minimumOutput;
		this.maximumOutput = maximumOutput;
	}

	/**
	 * Set the setpoint for the PID controller
	 *
	 * @param setpoint
	 *            the desired setpoint
	 */
	public void setSetpoint(double setpoint) {
		if (maximumInput > minimumInput)
			if (setpoint > maximumInput)
				this.setpoint = maximumInput;
			else if (setpoint < minimumInput)
				this.setpoint = minimumInput;
			else
				this.setpoint = setpoint;
		else
			this.setpoint = setpoint;
	}

	/**
	 * Getters for setpoint and error between sensor data and setpoint
	 */
	public double getSetpoint() {
		return setpoint;
	}
	public double getError() {
		return error;
	}

	/**
	 * Return true if the error is within the tolerance
	 *
	 * @return true if the error is less than the tolerance
	 */
	public boolean onTarget(double tolerance) {
		return lastInput != Double.NaN && Math.abs(lastInput - setpoint) < tolerance;
	}

	/**
	 * Reset all internal terms.
	 */
	public void reset() {
		lastInput = Double.NaN;
		prevError = 0;
		totalError = 0;
		result = 0;
		setpoint = 0;
	}

	public void resetIntegrator() {
		totalError = 0;
	}

	public String getState() {
		return "Kp: " + kP + "\nKi: " + kI + "\nKd: " + kD + "\n";
	}

	public String getType() {
		return "PIDController";
	}
}