package edu.wm.cs.cs301.duketran.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MazeBuilderEller extends MazeBuilder implements Runnable {
	protected Map<ArrayList<Integer>, Integer> cellToId;
	protected Map<Integer, Set<ArrayList<Integer>>> idToSet;
	// declare a variable that will be incremented with each new set to serve
	// as the set id
	private Integer setId; 

	public MazeBuilderEller() {
		super();
		System.out.println("MazeBuilderEller uses Eller's algorithm to generate maze.");
	}
	
	/**
	 * Test constructor to test the helper methods
	 * @param mode designate that it's in testing mode
	 */
	public MazeBuilderEller(String mode) {
		if (mode == "test") {
			cellToId = new HashMap<ArrayList<Integer>, Integer>();
			idToSet = new HashMap<Integer, Set<ArrayList<Integer>>>();
			setId = 1;
		}
	}
	
	/**
	 * Generates a maze using Eller's algorithm
	 * 1. For the first row, put each cell in its own set.
	 * 2. For each row, randomly pick and join adjacent cells that aren't in the same set. 
	 * 	  When joining adjacent cells, merge the two sets.
	 * 3. For each set, randomly create vertical connections to the next row. 
	 * 	  Each remaining set must have at least one vertical connection. 
	 * 	  Any cell in the next row that is connected must belong to the same set as the cell above it.
	 * 4. Any remaining unconnected cells in the next row must have their own sets.
	 * 5. Repeat until the last row is reached.
	 * 6. For the last row, join all adjacent cells that do not share a set.
	 * 	  Don't do any more vertical connections (because there's no row below).
	 */
	@Override
	protected void generatePathways() {
		// instantiate a cell-id HashMap to store each cell and its respective set
		// (default set value can be 0) and a id-set HashMap to store set ids
		// and their respective sets
		cellToId = new HashMap<ArrayList<Integer>, Integer>();
		idToSet = new HashMap<Integer, Set<ArrayList<Integer>>>();
		setId = 1;
				
		// iterate through the first row of the floorplan
		for (int x = 0; x < width; x++) {
			// add cell to its own new set and update the HashMaps
			newSet(x, 0);
			assert(cellToId.get(getCell(x, 0)) != null) : "Cell doesn't have its own set";
		}
		assert(cellToId.size() == width && idToSet.size() == width);
		
		// loop over the rows of the floorplan 0 -> height-2
		for (int y = 0; y < height-1; y++) {
			// loop over the columns of that row 0 -> width-2
			for (int x = 0; x < width-1; x++) {
				// check if the current cell and the adjacent are in different sets and whether
				// the current cell is in a room or generate a random integer within the range [0, 99]
				// to decide whether to tear down the eastern wall or not 
				// (less than 50 means no, greater than or equal to 50 means yes)
				if (!cellToId.get(getCell(x, y)).equals(cellToId.get(getCell(x+1, y))) &&
						(floorplan.isInRoom(x, y) || SingleRandom.getRandom().nextIntWithinInterval(0, 100) >= 50)) {
					// if it there is a wall to the east
					if (floorplan.hasWall(x, y, CardinalDirection.East)) { 
						Wallboard wallboard = new Wallboard(x, y, CardinalDirection.East);
						// if wallboard can be torn down (it's not load-bearing or a border)
						if (floorplan.canTearDown(wallboard)) {
							// tear down the eastern wall
							floorplan.deleteWallboard(wallboard);
							// update HashMaps
							mergeSets(x, y, CardinalDirection.East);
						}
					}
				}
			}
				
			// flags for current set and if a vertical connection 
			// has been made yet
			Integer currSet = 0;
			boolean newVerticalConnection = false;
			
			// loop over the columns of the current row
			for (int x = 0; x < width; x++) {
				Wallboard wallboard = new Wallboard(x, y, CardinalDirection.South);
				
				// determine if wallboard cannot be torn down (it's load-bearing or a border)
				// or if the given cell is not in a room and generate a random integer
				// within the range [0, 99] to decide whether to create a vertical connection 
				// (less than 50 means no, greater than or equal to 50 means yes)						
				if (!floorplan.canTearDown(wallboard) || (!floorplan.isInRoom(x, y) && 
					SingleRandom.getRandom().nextIntWithinInterval(0, 100) < 50)) {
					// if the cell in the current row (not the next one) is 
					// in a different set from the previous one in the same row,
					// then update the flags
					int tempSet = cellToId.get(getCell(x, y));
					if (tempSet != currSet) {
						currSet = tempSet;
						newVerticalConnection = false;
					}
				} else {
					// if there is a southern wall
					if (floorplan.hasWall(x, y, CardinalDirection.South)) {
						// tear down the southern wall
						floorplan.deleteWallboard(wallboard);
					}
					// update HashMaps
					mergeSets(x, y, CardinalDirection.South);
					// update flag
					newVerticalConnection = true;
					assert(cellToId.get(getCell(x, y)).equals(cellToId.get(getCell(x, y+1)))) : "Cells not in the same set";
				}
		
				// if this is the last cell in the set (the next cell is in a
				// different set) and a connection has not been made, then it must
				// be made with this cell
				if (!newVerticalConnection && (x == width-1 ||
						(x < width-1 && !currSet.equals(cellToId.get(getCell(x+1, y)))))) {
					// if there is a southern wall
					if (floorplan.hasWall(x, y, CardinalDirection.South)) {
						// tear down the southern wall
						floorplan.deleteWallboard(wallboard);
					}
					// update HashMaps
					mergeSets(x, y, CardinalDirection.South);
					// update flag
					newVerticalConnection = true;
				}
			}
				
			// loop over the columns of the next row
			for (int x = 0; x < width; x++) {
				// if a cell doesn't have a vertical connection or the cell is in a room and
				// it isn't in a set yet (it wasn't vertically connected to a cell in the 
				// previous row, make a new set for itself
				if (floorplan.hasWall(x, y+1, CardinalDirection.North) ||
					(floorplan.isInRoom(x, y+1) && !cellToId.containsKey(getCell(x, y+1)))) {
					// add cell to its own new set and update the HashMaps
					newSet(x, y+1);
				}
			}
		}
			
		// iterate through the last row of the floorplan 0 -> width-2
		// and put all of the cells into the same set
		for (int x = 0; x < width-1; x++) {
			ArrayList<Integer> origCell = getCell(x, height-1);
			ArrayList<Integer> newCell = getCell(x+1, height-1);
			
			Integer origId = cellToId.get(origCell);
			Integer newId = cellToId.get(newCell);
			if (!origId.equals(newId)) {
				Wallboard wallboard = new Wallboard(x, height-1, CardinalDirection.East);
				if (floorplan.canTearDown(wallboard)) {
					// tear down the eastern wall
					floorplan.deleteWallboard(wallboard);
					// update HashMaps
					mergeSets(x, height-1, CardinalDirection.East);
				}
			}
		}
		
		assert(idToSet.size() == 1) : "Something went wrong with the algorithm!";
	}
	
	// helper methods
	
	/**
	 * Converts the specified coordinates into an ArrayList
	 * @param x the x-coordinate of the specified cell
	 * @param y the y-coordinate of the specified cell
	 * @return an ArrayList containing the coordinates, or null if inputs are invalid
	 */
	protected ArrayList<Integer> getCell(int x, int y) {
		// check invalid inputs
		if (x >= width || y >= height || x < 0 || y < 0) {
			System.out.println("Error: inputs out of bounds");
			return null;
		}
		
		// make and return a new ArrayList containing the requested cell's coordinates
		ArrayList<Integer> cell = new ArrayList<Integer>();
		cell.add(0, x); cell.add(1, y);
		return cell;
	}
	
	/**
	 * Makes a new set from the specified cell and updates the HashMaps
	 * @param x the x-coordinate of the specified cell
	 * @param y the y-coordinate of the specified cell
	 */
	protected void newSet(int x, int y) {
		// check invalid inputs
		if (x >= width || y >= height || x < 0 || y < 0) {
			System.out.println("Error: inputs out of bounds");
			return;
		}
		
		// make a new set for each cell and add the cell to it
		ArrayList<Integer> cell = getCell(x, y);
		Set<ArrayList<Integer>> cells = new HashSet<ArrayList<Integer>>();
		cells.add(cell);
		
		// bind the cell to the id in the cell-id HashMap and
		// bind the id to the set in the id-set HashMap
		cellToId.put(cell, setId);
		idToSet.put(setId, cells);
		
		//increment the id
		setId++;
	}
	
	/**
	 * Merge the sets of the current cell and the adjacent cell in the
	 * specified direction
	 * @param x the x-coordinate of the specified cell
	 * @param y the y-coordinate of the specified cell
	 * @param dir the specified merge direction
	 */
	protected void mergeSets(int x, int y, CardinalDirection dir) {
		// check general invalid coordinates
		if (x >= width || y >= height || x < 0 || y < 0) {
			System.out.println("Error: inputs out of bounds");
			return;
		}
		
		ArrayList<Integer> origCell = new ArrayList<Integer>(); 
		ArrayList<Integer> newCell = new ArrayList<Integer>();
		
		switch (dir) {
			case East:
				// check specific invalid coordinates
				if (x == width-1) {
					System.out.println("Error: inputs out of bounds");
					return;
				}
				origCell = getCell(x, y);
				newCell = getCell(x+1, y);
				break;
			case South:
				// check specific invalid coordinates
				if (y == width-1) {
					System.out.println("Error: inputs out of bounds");
					return;
				}
				newSet(x, y+1);
				origCell = getCell(x, y);
				newCell = getCell(x, y+1);
				break;
			default:
				System.out.println("Error: invalid merge direction");
				return;
		}

		// merge the original set with the new set
		Integer origId = cellToId.get(origCell);
		Integer newId = cellToId.get(newCell);
		Set<ArrayList<Integer>> origCells = idToSet.get(origId);
		Set<ArrayList<Integer>> newCells = idToSet.get(newId);
		origCells.addAll(newCells);
		
		// for all of the new cells, update their ids to the new set's id
		for (ArrayList<Integer> cell: newCells) {
			assert(origCells.contains(cell)) : "Not all items were merged from the new set";
			cellToId.put(cell, origId);
		}
		assert(cellToId.get(origCell).equals(cellToId.get(newCell))) : "Sets didn't merge properly";
		
		// remove the new cell set
		idToSet.remove(newId);
		// replace the original set with the updated set
		idToSet.put(origId, origCells);
	}
}
	