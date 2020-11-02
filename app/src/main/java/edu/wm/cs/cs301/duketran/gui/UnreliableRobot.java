package edu.wm.cs.cs301.duketran.gui;

/**
 * @author Duke Tran
 * Class: UnreliableRobot
 * <p>
 * Responsibilities: perform move and rotate operations, interact with its sensors,
 * monitor and manage its energy consumption
 * <p>
 * Collaborators: Controller, RobotDriver (Wall-follower), DistanceSensor (ReliableSensor and UnreliableSensor)
 */
public class UnreliableRobot extends ReliableRobot {

	public UnreliableRobot(int f, int l, int r, int b) {
		super();
		leftSensor = (l == 0 ? new UnreliableSensor(Direction.LEFT) : new ReliableSensor(Direction.LEFT));
		rightSensor = (r == 0 ? new UnreliableSensor(Direction.RIGHT) : new ReliableSensor(Direction.RIGHT));
		forwardSensor = (f == 0 ? new UnreliableSensor(Direction.FORWARD) : new ReliableSensor(Direction.FORWARD));
		backwardSensor = (b == 0 ? new UnreliableSensor(Direction.BACKWARD) : new ReliableSensor(Direction.BACKWARD));
	}
}
