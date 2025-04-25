package ru.s21.rogue;

import java.util.Random;


public class Room {

    int X;
    int Y;
    int W;
    int H;
    int mark; // 0=unconnected, 1=connected, -1=dropped

    Room(int x, int y, int w, int h) {
        X = x;
        Y = y;
        W = w;
        H = h;
        mark = 0;
    }

    // Returns the screen coord of the room's center
    Coord center() {
        int x = X + W / 2;
        int y = Y + H / 2;
        return new Coord(x, y);
    }

    Coord topLeft() {
        return new Coord(X, Y);
    }

    // Returns a random point within the room ensuring it's not on a wall
    Coord randPoint() {
        Random rand = new Random();
        int x = X + rand.nextInt(W - 2) + 1;
        int y = Y + rand.nextInt(H - 2) + 1;
        return new Coord(x, y);
    }

    // Returns the coord of a random point on the wall of the given direction
    Coord randWallPoint(Direction dir) {
        Coord rndPt = randPoint();
        int x = rndPt.X;
        int y = rndPt.Y;

        switch (dir.direction) {
            case North:
                y = Y;
                break;
            case South:
                y = Y + H - 1;
                break;
            case East:
                x = X + W - 1;
                break;
            case West:
                x = X;
                break;
        }
        return new Coord(x, y);
    }

    // Updates the dimensions of the room
    void setSize(int x, int y, int w, int h) {
        X = x;
        Y = y;
        W = w;
        H = h;
    }

    // Returns true if the given x,y coord in within the bounds of the room
    boolean inRoom(Coord pos) {
        return X - 1 < pos.X &&
                pos.X < X + W + 1 &&
                Y - 1 < pos.Y &&
                pos.Y < Y + H + 1;
    }

}
