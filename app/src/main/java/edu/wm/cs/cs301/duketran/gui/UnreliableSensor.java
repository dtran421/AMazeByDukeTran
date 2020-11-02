package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.gui.Robot.Direction;

/**
 * @author Duke Tran
 * Class: UnreliableSensor
 * <p>
 * Responsibilities: sense distance to obstacle (wall), obtain energy cost for sensing,
 * determine when a failure occurs and start and stop the repair process
 * <p>
 * Collaborators: Maze, RobotDriver (Wall-follower and Wizard), Robot, RepairProcess
 */
public class UnreliableSensor extends ReliableSensor {
	protected Thread repairProcess;
	
	public UnreliableSensor() {
		super();
	}
	
	public UnreliableSensor(Direction direction) {
		super(direction);
	}

	/**
	 * Starts a concurrent, independent failure and repair process that fails the sensor and repairs it.
	 * @param meanTimeBetweenFailures is the mean time between failures in seconds, must be greater than zero
	 * @param meanTimeToRepair is the mean time to repair in seconds, must be greater than zero
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		try {
			// start the repair process thread
			RepairProcess rp = new RepairProcess(this, meanTimeBetweenFailures, meanTimeToRepair);
			repairProcess = new Thread(rp);
			repairProcess.start();
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			repairProcess = null;
		}
	}

	/**
	 * Stops a failure and repair process and restores the sensor in an operational state.
	 * If called after starting a process, it will stop the process as soon as the sensor is operational.
	 * If called with no running failure and repair process, it will throw an UnsupportedOperationException.
	 * @throws UnsupportedOperationException if called with no running failure and repair process
	 */
	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		// check that a thread exists and is currently running
		if (repairProcess != null && repairProcess.isAlive()) {
			// terminate the repair process
			repairProcess.interrupt();
			// set the sensor to operational
			setOperational(true);
			// clean up
			repairProcess = null;
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * Sets the operational status to the inputed value
	 * @param status used to update the sensor
	 */
	public void setOperational(boolean status) {
		isOperational = status;
	}
}
