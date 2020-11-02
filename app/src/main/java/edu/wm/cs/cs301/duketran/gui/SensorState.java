package edu.wm.cs.cs301.duketran.gui;

/**
 * @author Duke Tran
 * Interface: SensorState
 * <p>
 * Collaborators: RobotDriver (Wall-follower)
 * <p>
 * Implementing classes: OperationalState, RepairState
 */
public interface SensorState {
	/**
	 * Decides what action to take next (move or rotate) and executes it based on sensor information
	 */
	public boolean performNextAction();
}
