package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.generation.CardinalDirection;
import edu.wm.cs.cs301.duketran.generation.Maze;
import edu.wm.cs.cs301.duketran.gui.Constants.UserInput;

/**
 * @author Duke Tran
 * Class: ReliableRobot
 * <p>
 * Responsibilities: perform move and rotate operations, interact with its sensors,
 * monitor and manage its energy consumption
 * <p>
 * Collaborators: Controller, RobotDriver (Wall-follower and Wizard), DistanceSensor (ReliableSensor and UnreliableSensor)
 */
public class ReliableRobot implements Robot {
	protected StatePlaying controller;
	protected Maze maze;
	protected int width;
	protected int height;
	protected DistanceSensor leftSensor;
	protected DistanceSensor rightSensor;
	protected DistanceSensor forwardSensor;
	protected DistanceSensor backwardSensor;
		
	protected float batteryLevel;
	protected int distTraveled;
	protected boolean stopped;

	protected final float INITIAL_BATTERY = 3500;
	protected final float ROTATE_COST = 3;
	protected final float MOVE_COST = 6;
	protected final float JUMP_COST = 40;
	
	public ReliableRobot() {
		setBatteryLevel(INITIAL_BATTERY);
		leftSensor = new ReliableSensor(Direction.LEFT);
		rightSensor = new ReliableSensor(Direction.RIGHT);
		forwardSensor = new ReliableSensor(Direction.FORWARD);
		backwardSensor = new ReliableSensor(Direction.BACKWARD);
	}

	/**
	 * Sets the controller of the robot so that it can interact with and move within the maze.
	 * @param controller allows robot to interact with the maze
	 * @throws IllegalArgumentException if controller is null, 
	 * or if controller is not in playing state, 
	 * or if controller does not have a maze
	 */
	@Override
	public void setController(StatePlaying controller) throws IllegalArgumentException {
		// check if controller is null, controller is not in playing state, or it doesn't have a maze
		// and throw an exception if so
		if (controller == null || controller.getMazeConfiguration() == null)
			throw new IllegalArgumentException();
		// assign controller field to inputed controller
		this.controller = controller;
		// reset the robot's fields
		stopped = false;
		setBatteryLevel(INITIAL_BATTERY);
		resetOdometer();
		// set the maze field to the new maze and assign it to the sensors
		maze = controller.getMazeConfiguration();
		width = maze.getWidth();
		height = maze.getHeight();
		leftSensor.setMaze(maze);
		rightSensor.setMaze(maze);
		forwardSensor.setMaze(maze);
		backwardSensor.setMaze(maze);
		
	}

	/**
	 * Provides the current maze position as [x, y].
	 * @return array of the x- and y-coordinates
	 * @throws Exception if position is outside of the maze
	 */
	@Override
	public int[] getCurrentPosition() throws Exception {
		int[] currPos = controller.getCurrentPosition();
		// check if the current position is outside of the maze and throw an exception if so
		if (currPos[0] < 0 || currPos[0] >= width || currPos[1] < 0 || currPos[1] >= height) 
			throw new Exception();
		// return position array
		return currPos;
	}

	/**
	 * Provides the robot's current direction.
	 * @return cardinal direction that the robot is currently facing
	 */	
	@Override
	public CardinalDirection getCurrentDirection() {
		// return fetched direction from the controller
		return controller.getCurrentDirection();
	}

	/**
	 * Provides the current battery level.
	 * If the battery level is 0, then robot stops.
	 * @return current battery level
	 */
	@Override
	public float getBatteryLevel() {
		// return the battery level
		return batteryLevel;
	}

	/**
	 * Sets the current battery level.
	 * @param level is the current battery level
	 * @throws IllegalArgumentException if level is negative 
	 */
	@Override
	public void setBatteryLevel(float level) throws IllegalArgumentException {
		// check if level is negative and throw an exception if so
		if (level < 0) throw new IllegalArgumentException();
		// set the battery level to the inputed level
		batteryLevel = level;
	}

	/**
	 * Provides the energy consumption for a full 360 degree rotation.
	 * @return energy for a full rotation
	 */
	@Override
	public float getEnergyForFullRotation() {
		// return the energy required for a full rotation
		return 4 * ROTATE_COST;
	}

	/**
	 * Provides the energy consumption for moving forward for a distance of 1 step.
	 * @return energy for a single step forward
	 */
	@Override
	public float getEnergyForStepForward() {
		// return the energy required for a single step forward
		return MOVE_COST;
	}

	/** 
	 * Provides the distance traveled by the robot.
	 * @return the distance traveled measured in single-cell steps forward
	 */
	@Override
	public int getOdometerReading() {
		// return the distance traveled
		return distTraveled;
	}

	/** 
     * Resets the distance traveled to zero.
     */
	@Override
	public void resetOdometer() {
		// set the distance traveled to zero
		distTraveled = 0;
	}

	/**
	 * Turn robot on the spot for amount of degrees. If robot runs out of energy, it stops.
	 * @param turn is the direction to turn, which is relative to the current direction. 
	 */
	@Override
	public void rotate(Turn turn) {
		// check battery level beforehand
		if (getBatteryLevel() < ROTATE_COST || (turn == Turn.AROUND && getBatteryLevel() < ROTATE_COST * 2)) {
			setBatteryLevel(0);
			stopped = true;
			return;
		}
		// use the controller to turn the robot and update the battery level
		switch (turn) {
			case LEFT:
				setBatteryLevel(getBatteryLevel()-ROTATE_COST);
				controller.keyDown(UserInput.Left, 0);
				break;
			case RIGHT:
				setBatteryLevel(getBatteryLevel()-ROTATE_COST);
				controller.keyDown(UserInput.Right, 0);
				break;
			case AROUND:
				setBatteryLevel(getBatteryLevel()-ROTATE_COST*2);
				controller.keyDown(UserInput.Left, 0);
				controller.keyDown(UserInput.Left, 0);
				break;
		}
		// check if the battery level is 0 and stop the robot if so
		if (getBatteryLevel() == 0) stopped = true;
	}

	/**
	 * Moves robot forward a given number of steps. If the robot runs out of energy at any point, then it stops. 
	 * If the robot hits an obstacle like a wall, it remains at the position in front of the obstacle and stops.
	 * @param distance is the number of cells to move in the robot's current direction 
	 * @throws IllegalArgumentException if distance not positive
	 */
	@Override
	public void move(int distance) throws IllegalArgumentException {
		// check if distance is not positive and throw an exception if so
		if (distance < 0) throw new IllegalArgumentException();
		// check battery level beforehand
		if (getBatteryLevel() < MOVE_COST) {
			setBatteryLevel(0);
			stopped = true;
			return;
		}
		// create a new variable to keep track of distance moved
		int distMoved = 0;
		// while the distance moved is less than the inputed distance
		while (distMoved < distance) {
			try {
				// get the current position
				int[] currPos = getCurrentPosition();
				// check if there is an obstacle (wall) directly in front of the robot
				// and stop if so
				if (distMoved != distance && maze.hasWall(currPos[0], currPos[1], getCurrentDirection())) {
					setBatteryLevel(0);
					stopped = true;
					break;
				}
			} catch (Exception e) {
				System.out.println("Position outside maze!");
				return;
			}
			// move the robot one step forward and update the distance traveled and battery level
			controller.keyDown(UserInput.Up, 0);
			distTraveled++;
			distMoved++;
			setBatteryLevel(getBatteryLevel()-MOVE_COST);	
			// check if the energy has been depleted and stop if so
			// (make sure to break out of the loop)
			if (getBatteryLevel() == 0) {
				stopped = true;
				break;
			}
			// check if there's enough energy to continue moving
			if (distMoved != distance && getBatteryLevel() < MOVE_COST) {
				setBatteryLevel(0);
				stopped = true;
				return;
			}
		}
	}

	/**
	 * Makes robot move one step in a forward direction even if there is a wall
	 * in front of it (jumps over the wall). If the robot runs out of energy at any point, then it stops.
	 * If the robot tries to jump over an exterior wall (it would land outside of the maze),
	 * it remains at its current location and direction and stops.
	 */
	@Override
	public void jump() {
		// check battery level beforehand
		if (getBatteryLevel() < JUMP_COST) {
			setBatteryLevel(0);
			stopped = true;
			return;
		}
		try {
			// fetch the current position of the robot
			int[] currPos = getCurrentPosition();
			// check if the wall in front is an exterior wall and stop if so
			switch (getCurrentDirection()) {
			case North:
				if (maze.hasWall(currPos[0], currPos[1], CardinalDirection.North) && currPos[1]-1<0) {
					setBatteryLevel(0);
					stopped = true;
					return;
				}	
				break;
			case East:
				if (maze.hasWall(currPos[0], currPos[1], CardinalDirection.East) && currPos[0]+1==width) {
					setBatteryLevel(0);
					stopped = true;
					return;
				}
				break;
			case South:
				if (maze.hasWall(currPos[0], currPos[1], CardinalDirection.South) && currPos[1]+1==height) {
					setBatteryLevel(0);
					stopped = true;
					return;
				}
				break;
			case West:
				if (maze.hasWall(currPos[0], currPos[1], CardinalDirection.West) && currPos[0]-1<0) {
					setBatteryLevel(0);
					stopped = true;
					return;
				}
				break;
			}	
		} catch (Exception e) {
			System.out.println("Position outside maze!");
			return;
		}
		// execute the jump and update the distance traveled and battery level
		controller.keyDown(UserInput.Jump, 0);
		distTraveled++;
		setBatteryLevel(getBatteryLevel()-JUMP_COST);
		if (getBatteryLevel() == 0) stopped = true;
	}
	
	/**
	 * Tells whether the current position is at the exit (inside the maze). 
	 * @return if robot is at the exit
	 */
	@Override
	public boolean isAtExit() {
		// fetch the maze from the controller and determine if the current position is at the exit
		// by using the floorplan's isExitPosition method
		int[] currPos;
		try {
			currPos = getCurrentPosition();
		} catch (Exception e) {
			System.out.println("Position outside maze!");
			return false;
		}
		return controller.getMazeConfiguration().getFloorplan().isExitPosition(currPos[0], currPos[1]);
	}

	/**
	 * Tells if current position is inside a room. 
	 * @return if robot is inside a room
	 */	
	@Override
	public boolean isInsideRoom() {
		// return whether the current position is inside a room from the controller
		int[] currPos;
		try {
			currPos = getCurrentPosition();
		} catch (Exception e) {
			System.out.println("Position outside maze!");
			return false;
		}
		return controller.getMazeConfiguration().getFloorplan().isInRoom(currPos[0], currPos[1]);
	}

	/**
	 * Tells if the robot has stopped.
	 * @return if the robot has stopped
	 */
	@Override
	public boolean hasStopped() {
		// returns stopped
		return stopped;
	}

	/**
	 * Provides the distance to an obstacle (wall) in the given direction.
	 * The direction is relative to the robot's current direction.
	 * @param direction specifies the sensor direction
	 * @return number of steps towards obstacle, Integer.MAX_VALUE if we're facing the exit (no wall, goes to eternity)
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 * or the sensor exists but is currently not operational (failure or battery depleted)
	 */
	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		// keep track of the current cell
		int[] currPos;
		try {
			currPos = getCurrentPosition();
		} catch (Exception e) {
			System.out.println("Position outside maze!");
			return -1;
		}
		float[] batteryLevel = {getBatteryLevel()};
		int dist = -1;
		try {
			switch (direction) {
			case LEFT:
				dist = leftSensor.distanceToObstacle(currPos, getCurrentDirection(), batteryLevel);
				break;
			case RIGHT:
				dist = rightSensor.distanceToObstacle(currPos, getCurrentDirection(), batteryLevel);
				break;
			case FORWARD:
				dist = forwardSensor.distanceToObstacle(currPos, getCurrentDirection(), batteryLevel);
				break;
			case BACKWARD:
				dist = backwardSensor.distanceToObstacle(currPos, getCurrentDirection(), batteryLevel);
				break;
			}	
		} catch (Exception e) {
			String msg = e.getMessage();
			System.out.println(msg);
			if (msg == "PowerFailure") {
				setBatteryLevel(0);
				stopped = true;
			}
			throw new UnsupportedOperationException(msg);
		}
		setBatteryLevel(batteryLevel[0]);
		if (getBatteryLevel() == 0) stopped = true;
		return dist;
	}

	/**
	 * Tells if a sensor can identify the exit from the current position in the given direction.
	 * The direction is relative to the robot's current direction.
	 * @param direction specifies the sensor direction
	 * @return true if the exit of the maze is visible in a straight line of sight
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 * or the sensor exists but is currently not operational (failure or battery depleted)
	 */
	@Override
	public boolean canSeeThroughTheExitIntoEternity(Direction direction) throws UnsupportedOperationException {
		try {
			// return true if a wallboard wasn't encountered (looking at the exit), false otherwise
			return distanceToObstacle(direction) == Integer.MAX_VALUE ? true : false;
		} catch (Exception e) {
			String msg = e.getMessage();
			throw new UnsupportedOperationException(msg);
		}
	}

	/**
	 * Starts a concurrent, independent failure and repair process that fails the sensor and repairs it.
	 * @param direction of a given sensor
	 * @param meanTimeBetweenFailures is the mean time between failures in seconds, must be greater than zero
	 * @param meanTimeToRepair is the mean time to repair in seconds, must be greater than zero
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		try {
			switch (direction) {
			case LEFT:
				leftSensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
				break;
			case RIGHT:
				rightSensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
				break;
			case FORWARD:
				forwardSensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
				break;
			case BACKWARD:
				backwardSensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
				break;
			}
		} catch (UnsupportedOperationException e) {
			System.out.println("Method not supported!");
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Stops a failure and repair process and restores the sensor in an operational state.
	 * If called after starting a process, it will stop the process as soon as the sensor is operational.
	 * If called with no running failure and repair process, it will return an UnsupportedOperationException.
	 * @param direction of a given sensor
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		try {
			switch (direction) {
			case LEFT:
				leftSensor.stopFailureAndRepairProcess();
				break;
			case RIGHT:
				rightSensor.stopFailureAndRepairProcess();
				break;
			case FORWARD:
				forwardSensor.stopFailureAndRepairProcess();
				break;
			case BACKWARD:
				backwardSensor.stopFailureAndRepairProcess();
				break;
			}
		} catch (UnsupportedOperationException e) {
			System.out.println("Method not supported!");
			throw new UnsupportedOperationException();
		}
	}
}
