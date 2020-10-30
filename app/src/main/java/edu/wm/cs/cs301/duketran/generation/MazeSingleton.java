package edu.wm.cs.cs301.duketran.generation;

public class MazeSingleton {
    private static MazeSingleton MAZE_SINGLETON = null;
    private static Maze maze;

    private MazeSingleton() {}

    public static MazeSingleton getInstance() {
        if (MAZE_SINGLETON == null) {
            synchronized (MazeSingleton.class) {
                MAZE_SINGLETON = new MazeSingleton();
            }
        }
        return MAZE_SINGLETON;
    }

    public Maze getMaze() {
        return maze;
    }

    public void setMaze(Maze mazeConfig) {
        maze = mazeConfig;
    }
}
