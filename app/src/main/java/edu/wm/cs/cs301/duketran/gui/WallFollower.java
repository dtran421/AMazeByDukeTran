package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.gui.Robot.Direction;
import edu.wm.cs.cs301.duketran.gui.Robot.Turn;

/**
 * @author Duke Tran
 * Class: WallFollower
 * <p>
 * Responsibilities: direct the robot towards the maze exit
 * <p>
 * Collaborators: Maze, Robot (ReliableRobot and UnreliableRobot), SensorState (RepairState, OperationalState)
 */
public class WallFollower extends Wizard {
	// keep track of the state of the sensors (OperationalState when all sensors are operational, RepairState
	// when at least one sensor is under repair)
	protected SensorState sensorState;
	
	/**
	 * Drives the robot towards the exit using the left-wall-follower algorithm.
	 * @return true if WallFollower successfully reaches the exit
	 * @throws Exception thrown if robot stopped (lack of energy, hit a wall, or somehow ended outside the maze
	 * without passing the exit)
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		// while the robot hasn't stopped
		while (!robot.hasStopped()) {
			int[] currPos;
			// try to get to the exit by running drive1Step2Exit using the wall-follower algorithm
			try {
				Thread.sleep(500);
				drive1Step2Exit();
				currPos = robot.getCurrentPosition();
			} catch (Exception e) {
				throw new Exception();
			}
			// check if it has reached the exit and return true if so
			if (robot.isAtExit()) {
				// cross the exit to win the game
				crossExit2Win(currPos);
				return true;
			}
		}
		return false;
	}

	/**
	 * Drives the robot one step towards the exit. Makes decision according to the wall-follower algorithm.
	 * @return true if driver successfully performed one step
	 * @throws Exception thrown if robot stopped (lack of energy, hit a wall, or somehow ended outside the maze
	 * without passing the exit)
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		// if robot is stopped, then throw an exception
		if (robot.hasStopped()) throw new Exception();
		
		// keep track of whether the robot moved one step
		boolean moved = false;
		// determine if the left sensor or forward sensor is under repair and change the state to RepairState if so
		boolean forwardStatus = isOperational(Direction.FORWARD);
		boolean leftStatus = isOperational(Direction.LEFT);
		if (!forwardStatus || !leftStatus) {
			boolean[] sensors = {forwardStatus, leftStatus, isOperational(Direction.RIGHT), isOperational(Direction.BACKWARD)};
			if (!sensors[0] && !sensors[1] && !sensors[2] && !sensors[3])
				waitForOperationalSensor(sensors);
			setState(sensors[0], sensors[1], sensors[2], sensors[3]);
		}
		else
			setState(true, true, true, true);

		// depending on the current state, perform the next action based on the operational sensors
		moved = sensorState.performNextAction();
		// if robot is stopped, then throw an exception
		if (robot.hasStopped()) throw new Exception();
		return moved;
	}
	
	/**
	 * Rotates the robot to face the exit and then move one step to step past the exit and win
	 * @param currentPosition of the robot (exit)
	 */
	@Override
	protected void crossExit2Win(int[] currentPosition) {
		// while the robot cannot see through the exit, rotate it to the left
		while (!robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD)) {
			robot.rotate(Turn.LEFT);
		}
		robot.move(1);
	}
	
	/**
	 * Determines if the sensor in the inputed direction is operational using the robot's distance to obstacle method
	 * @param direction to check
	 * @return whether it's operational
	 */
	protected boolean isOperational(Direction direction) {
		try {
			// if no exception is thrown, then the sensor is operational
			robot.distanceToObstacle(direction);
			return true;
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}
	
	/**
	 * Sets the state of Wall-Follower to either the OperationalState or the RepairState
	 * @param f status of the forward sensor
	 * @param l status of the left sensor
	 * @param r status of the right sensor
	 * @param b status of the backward sensor
	 */
	protected void setState(boolean f, boolean l, boolean r, boolean b) {
		sensorState = (f && l && r && b) ? new OperationalState(robot) : new RepairState(robot, f, l, r, b);
	}
	
	/**
	 * Waits for a sensor to become operational before proceeding
	 */
	protected void waitForOperationalSensor(boolean[] sensors) {
		while (!sensors[0] && !sensors[1] && !sensors[2] && !sensors[3]) {
			try {
				Thread.sleep(2000);
				sensors[0] = isOperational(Direction.FORWARD);
				sensors[1] = isOperational(Direction.LEFT);
			} catch (InterruptedException e) {
				System.err.println("Something went wrong!");
				return;
			}
		}
	}

}
