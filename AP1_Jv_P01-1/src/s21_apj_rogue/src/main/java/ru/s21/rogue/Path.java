package ru.s21.rogue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Path {
    // Need to rename all other Path (room connections)
    ArrayList<Coord> steps;
    String algo;
    int iter;

    public Path(String algo, int iter) {
        this.algo = algo;
        this.iter = iter;
        steps = new ArrayList<Coord>();
    }

    String String() {
        return String.format("len=%d, algo=%s, iter=%d", steps.size(), algo, iter);
    }

    // A simple Breadth First Seach pathfinding algorithm.  Using A* would be
// more optimal but the complexity is low for this game (small map, only
// a few monsters chasing at any given time.)
// https://www.redblobgames.com/pathfinding/a-star/introduction.html
    static Path findPathBFS(DungeonMap dm, Coord start, Coord end) {
        // Declarations
        CoordQueue frontier = new CoordQueue();
        HashMap<Coord, Coord> cameFrom = new HashMap<>();
        int pathCount = 0;

        // Initialize
        frontier.add(start);
        cameFrom.put(start, start);
        //FIXA
        // While path not found yet or no more explorable areas
        Coord foundPath = cameFrom.get(end);
        while (!frontier.isEmpty() && !(foundPath == null)) {
            Coord current = frontier.next();

            ArrayList<Coord> nb = dm.getWalkableNeighbours(current);
            for (Coord next : nb) {
                Coord reached = cameFrom.get(next);
                if (!(reached == null)) {
                    frontier.add(next);
                    cameFrom.put(next, current);
                }
            }
            foundPath = cameFrom.get(end);
            pathCount++;
        }

        //debug.Add("path found: %d steps", pathCount)

        // Build a slice to hold the path we found
        Path path = new Path(
                "bfs",
                pathCount
        );
        boolean ok;
        Coord current = end;
        while (!(current.equals(start))) {
            path.steps.add(current);
            current = cameFrom.get(current);
            if (current == null) {
                break;
            }
        }
        Collections.reverse(path.steps);
        return path;
    }

    // -----------------------------------------------------------------------
    void drawPathDebug(Display disp, Path path, char ch) {
        for (Coord pos : steps) {
            disp.SetContent(pos.X, pos.Y + 1, ch, null, "debug2");
        }
    }


}
