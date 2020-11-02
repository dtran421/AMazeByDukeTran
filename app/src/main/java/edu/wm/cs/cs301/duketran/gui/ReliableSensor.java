package edu.wm.cs.cs301.duketran.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.wm.cs.cs301.duketran.generation.Maze;
import edu.wm.cs.cs301.duketran.generation.CardinalDirection;
import edu.wm.cs.cs301.duketran.gui.Robot.Direction;

/**
 * @author Duke Tran
 * Class: ReliableSensor
 * <p>
 * Responsibilities: sense distance to obstacle (wall), obtain energy cost for sensing
 * <p>
 * Collaborators: Maze, RobotDriver (Wall-follower and Wizard), Robot
 */
public class ReliableSensor implements DistanceSensor {
	protected Maze maze;
	protected int width;
	protected int height;
	protected Direction mountedDirection;
	protected boolean isOperational;
	
	protected final float SENSE_COST = 1;
	
	protected Map<ArrayList<Integer>, CardinalDirection> coordsMap;
	protected Map<CardinalDirection, ArrayList<Integer>> dirsMap;
	
	public ReliableSensor() {
		isOperational = true;
		// instantiate the maps that will be used to convert a relative direction to its absolute cardinal direction
		createMappings();
	}
	
	public ReliableSensor(Direction direction) {
		this();
		setSensorDirection(direction);		
	}

	/**
	 * Tells the distance to an obstacle (wall) that the sensor
	 * measures.
	 * 
	 * This method requires that the sensor has been given a reference
	 * to the current maze and a mountedDirection.
	 * 
	 * @param currentPosition is the current location as (x,y) coordinates
	 * @param currentDirection specifies the direction of the robot
	 * @param powersupply is an array of length 1 containing the current battery level
	 * of the robot
	 * @return number of steps towards obstacle or Integer.MAX_VALUE if there's no obstacle 
	 * (the exit is visible).
	 * @throws IllegalArgumentException if any parameter is null
	 * or if currentPosition is outside of the maze
	 * @throws IndexOutOfBoundsException if the battery level is less than 0
	 * @throws Exception with message 
	 * SensorFailure if the sensor is currently not operational
	 * PowerFailure if the power supply is insufficient for the operation
	 */
	@Override
	public int distanceToObstacle(int[] currentPosition, CardinalDirection currentDirection, float[] powersupply)
		throws Exception {
		// check if any of the parameters are null or if currentPosition is outside of the maze
		// and throw an exception if so
		if (currentPosition == null || currentDirection == null || powersupply == null ||
			currentPosition[0] < 0 || currentPosition[0] >= width || currentPosition[1] < 0 || currentPosition[1] >= height)
			throw new IllegalArgumentException();
		// check if the powersupply is less than 0 and throw an exception if so
		if (powersupply[0] < 0) throw new IndexOutOfBoundsException();
		// check if sensor is not operational or the powersupply is insufficient
		// and throw the appropriate exception with message if so
		if (!isOperational) throw new Exception("SensorFailure");
		if (powersupply[0] < getEnergyConsumptionForSensing()) throw new Exception("PowerFailure");		
	
		// figure out which absolute (cardinal) direction we should move in
		CardinalDirection currDir = convertToAbsoluteDirection(mountedDirection, currentDirection);
		// keep track of the distance with a counter
		int dist = 0;
		// while there isn't a wallboard in the given direction
		while (!maze.hasWall(currentPosition[0], currentPosition[1], currDir)) {
			dist = senseDistance(currentPosition, powersupply, currDir, dist);
			if (dist == Integer.MAX_VALUE) return dist;
		}
		powersupply[0] -= getEnergyConsumptionForSensing();
		return dist;
	}

	protected int senseDistance(int[] currentPosition, float[] powersupply, CardinalDirection currDir, int dist)
			throws Exception {
		// check if the next cell is outside of the maze (meaning the current cell is located at the exit) and
		// return Integer.MAX_VALUE if so
		// else sense the next cell and update the distance counter
		switch (currDir) {
			case North:
				if (currentPosition[1]-1 < 0) {
					powersupply[0] -= getEnergyConsumptionForSensing();
					if (powersupply[0] < getEnergyConsumptionForSensing()) throw new Exception("PowerFailure");
					return Integer.MAX_VALUE;
				}
				currentPosition[1] -= 1;
				break;
			case East:
				if (currentPosition[0]+1 >= width) {
					powersupply[0] -= getEnergyConsumptionForSensing();
					if (powersupply[0] < getEnergyConsumptionForSensing()) throw new Exception("PowerFailure");
					return Integer.MAX_VALUE;
				}
				currentPosition[0] += 1;
				break;
			case South:
				if (currentPosition[1]+1 >= height) {
					powersupply[0] -= getEnergyConsumptionForSensing();
					if (powersupply[0] < getEnergyConsumptionForSensing()) throw new Exception("PowerFailure");
					return Integer.MAX_VALUE;
				}
				currentPosition[1] += 1;
				break;
			case West:
				if (currentPosition[0]-1 < 0) {
					powersupply[0] -= getEnergyConsumptionForSensing();
					if (powersupply[0] < getEnergyConsumptionForSensing()) throw new Exception("PowerFailure");
					return Integer.MAX_VALUE;
				}
				currentPosition[0] -= 1;
				break;
		}
		dist++;
		return dist;
	}

	/**
	 * Assigns the maze to the sensor.
	 * @param maze the maze for this game
	 * @throws IllegalArgumentException if parameter is null
	 * or if it does not contain a floor plan
	 */
	@Override
	public void setMaze(Maze maze) {
		// check if the maze or its floor plan is null and throw an exception if so
		if (maze == null || maze.getFloorplan() == null) throw new IllegalArgumentException();
		// assign the maze parameter to the maze field of the sensor
		this.maze = maze;
		// use the maze to fetch the dimensions
		width = maze.getWidth();
		height = maze.getHeight();
	}

	/**
	 * Assigns the relative direction at which the sensor is mounted on the robot.
	 * @param mountedDirection is the sensor's relative direction
	 * @throws IllegalArgumentException if parameter is null
	 */
	@Override
	public void setSensorDirection(Direction mountedDirection) {
		// check if mountedDirection is null and throw an exception if so
		if (mountedDirection == null) throw new IllegalArgumentException();
		// assign the mountedDirection parameter to the mountedDirection field of the sensor
		this.mountedDirection = mountedDirection;
	}

	/**
	 * Returns the amount of energy the sensor uses for calculating the distance to an obstacle exactly once.
	 * This amount is a fixed constant for the sensor.
	 * @return the amount of energy used for using the sensor once
	 */
	@Override
	public float getEnergyConsumptionForSensing() {
		// return the constant amount of energy required to use the sensor once
		return SENSE_COST;
	}

	/**
	 * Starts a concurrent, independent failure and repair process that fails the sensor and repairs it.
	 * For P3, this method won't be implemented and will just throw an exception.
	 * @param meanTimeBetweenFailures is the mean time between failures in seconds, must be greater than zero
	 * @param meanTimeToRepair is the mean time to repair in seconds, must be greater than zero
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Stops a failure and repair process and restores the sensor in an operational state.
	 * If called after starting a process, it will stop the process as soon as the sensor is operational.
	 * If called with no running failure and repair process, it will return an UnsupportedOperationException.
	 * For P3, this method won't be implemented and will just throw an exception.
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates the maps to store the mappings used in the convertToAbsoluteDirection method
	 */
	protected void createMappings() {
		// all of the CardinalDirections in the order that will map consistently to the transformations
		CardinalDirection[] dirs = {CardinalDirection.North, CardinalDirection.West, CardinalDirection.East, CardinalDirection.South};
		// map coordinates involving only +/-1 to each direction 
		coordsMap = new HashMap<ArrayList<Integer>, CardinalDirection>();
		// map each direction to its coordinates
		dirsMap = new HashMap<CardinalDirection, ArrayList<Integer>>();
		int idx = 0;
		int[] range = {-1, 1};
		for (int x = 0; x <= 1; x++) {
			for (int y = 0; y <= 1; y++) {
				ArrayList<Integer> pair = new ArrayList<Integer>();
				pair.add(range[x]); pair.add(range[y]);
				coordsMap.put(pair, dirs[idx]);
				dirsMap.put(dirs[idx], pair);
				idx++;
			}
		}
	}
	
	/**
	 * Converts a relative direction to an absolute direction based on the current CardinalDirection
	 * @param direction that we want to use for the conversion
	 * @param currDir is the current direction of the robot, we use this in conjunction with the relative direction
	 * to obtain the new absolute (cardinal) direction
	 * @return CardinalDirection of the relative direction
	 */
	protected CardinalDirection convertToAbsoluteDirection(Direction direction, CardinalDirection currDir) {
		// apply transformations to the CardinalDirection based on the relative direction
		assert(dirsMap != null);
		ArrayList<Integer> dirCoords = dirsMap.get(currDir); 
		ArrayList<Integer> newCoords = new ArrayList<Integer>();
		switch (direction) {
			case BACKWARD:
				newCoords.add(dirCoords.get(0)*-1); newCoords.add(dirCoords.get(1)*-1);
				break;
			case LEFT:
				newCoords.add(dirCoords.get(1)*-1); newCoords.add(dirCoords.get(0));
				break;
			case RIGHT:
				newCoords.add(dirCoords.get(1)); newCoords.add(dirCoords.get(0)*-1);
				break;
			default:
				return currDir;
		}
		return coordsMap.get(newCoords);
	}
}