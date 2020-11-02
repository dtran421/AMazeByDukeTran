package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.gui.Robot.Direction;
import edu.wm.cs.cs301.duketran.gui.Robot.Turn;

/**
 * @author Duke Tran
 * Class: RepairState
 * <p>
 * Responsibilities: represents the driver state in which at least one sensor is not operational (under repair),
 * keeps track of sensors that are under repair and fetches the closest operational sensor
 * <p>
 * Collaborators: RobotDriver (WallFollower)
 */
public class RepairState implements SensorState {
	private Robot robot;
	private boolean forwardStatus;
	private boolean leftStatus;
	private boolean rightStatus;
	private boolean backwardStatus;
	
	public RepairState(Robot robot, boolean f, boolean l, boolean r, boolean b) {
		this.robot = robot;
		forwardStatus = f;
		leftStatus = l;
		rightStatus = r;
		backwardStatus = b;
	}
	
	/**
	 * Decides what action to take next (move or rotate) and executes it based on sensor information
	 */
	@Override
	public boolean performNextAction() {
		// find the next operational sensor for the left and forward directions
		Direction workingSensorForForward = nextOperationalSensor(Direction.FORWARD);
		Direction workingSensorForLeft = nextOperationalSensor(Direction.LEFT);
		
		// rotate robot to accommodate the substitute sensor
		substituteSensor(Direction.LEFT, workingSensorForLeft);
		// detect if there's no wall to the left of the robot (distance to obstacle is 0)
		if (robot.distanceToObstacle(workingSensorForLeft) != 0) {
			// rotate robot back to its original orientation
			substituteSensor(workingSensorForLeft, Direction.LEFT);
			// turn left and take one step forward
			robot.rotate(Turn.LEFT);
			robot.move(1);
			return true;
		} else substituteSensor(workingSensorForLeft, Direction.LEFT);
		substituteSensor(Direction.FORWARD, workingSensorForForward);
		// detect if there's no wall in front of the robot
		if (robot.distanceToObstacle(workingSensorForForward) != 0) {
			substituteSensor(workingSensorForForward, Direction.FORWARD);
			// take one step forward
			robot.move(1);
			return true;
		} else substituteSensor(workingSensorForForward, Direction.FORWARD);
		
		// if none of the above, then turn right
		robot.rotate(Turn.RIGHT);
		return false;
	}
	
	// private methods
	
	/**
	 * Determines the closest operational sensor to the inputed direction
	 * @param direction that we are examining (its sensor may or may not be operational)
	 * @return the direction of the closest operational sensor
	 */
	private Direction nextOperationalSensor(Direction direction) {
		// throw an exception if all sensors are being repaired
		if (!forwardStatus && !leftStatus && !rightStatus && !backwardStatus)
			return null;
		
		// determine whether the sensor in the inputed direction is operational
		if (getSensorStatus(direction))
			// if yes, return the direction of that sensor
			return direction;
		// else check for the next operational sensor by checking the sensors to the left and right
		// of the inputed direction and then the back sensor
		else {
			switch (direction) {
				case FORWARD: 
					if (leftStatus) return Direction.LEFT;
					if (rightStatus) return Direction.RIGHT;
					if (backwardStatus) return Direction.BACKWARD;
				case LEFT:
					if (backwardStatus) return Direction.BACKWARD;
					if (forwardStatus) return Direction.FORWARD;
					if (rightStatus) return Direction.RIGHT;
				case RIGHT:
					if (forwardStatus) return Direction.FORWARD;
					if (backwardStatus) return Direction.BACKWARD;
					if (leftStatus) return Direction.LEFT;
				case BACKWARD: 
					if (rightStatus) return Direction.RIGHT;
					if (leftStatus) return Direction.LEFT;
					if (forwardStatus) return Direction.FORWARD;
				default:
					// throw an exception if direction is invalid
					throw new UnsupportedOperationException();
			}
		}		
	}
	
	/**
	 * Provides the operational status of a sensor in the inputed direction
	 * @param direction of the sensor being examined
	 * @return whether the sensor is operational
	 */
	private boolean getSensorStatus(Direction direction) {
		switch (direction) {
			case FORWARD:
				return forwardStatus;
			case LEFT:
				return leftStatus;
			case RIGHT:
				return rightStatus;
			case BACKWARD:
				return backwardStatus;
			default:
				return false;
		}
	}
	
	/**
	 * Rotates the robot so that it's inoperative sensor is replaced with an operational one
	 * @param current sensor
	 * @param substitute is the nearest operational sensor
	 */
	private void substituteSensor(Direction current, Direction substitute) {
		switch (current) {
			case FORWARD:
				if (substitute == Direction.LEFT) robot.rotate(Turn.RIGHT);
				else if (substitute == Direction.RIGHT) robot.rotate(Turn.LEFT);
				else if (substitute == Direction.BACKWARD) robot.rotate(Turn.AROUND);
				break;
			case LEFT:
				if (substitute == Direction.BACKWARD) robot.rotate(Turn.RIGHT);
				else if (substitute == Direction.FORWARD) robot.rotate(Turn.LEFT);
				else if (substitute == Direction.RIGHT) robot.rotate(Turn.AROUND);
				break;
			case RIGHT:
				if (substitute == Direction.FORWARD) robot.rotate(Turn.RIGHT);
				else if (substitute == Direction.BACKWARD) robot.rotate(Turn.LEFT);
				else if (substitute == Direction.LEFT) robot.rotate(Turn.AROUND);
				break;
			case BACKWARD:
				if (substitute == Direction.RIGHT) robot.rotate(Turn.RIGHT);
				else if (substitute == Direction.LEFT) robot.rotate(Turn.LEFT);
				else if (substitute == Direction.FORWARD) robot.rotate(Turn.AROUND);
				break;
		}
	}
}
