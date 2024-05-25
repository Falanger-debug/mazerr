package com.falanger.mazerr.Maze;

public class Edge {
    int x1, y1, x2, y2;
    int weight;

    Edge(int x1, int y1, int x2, int y2, int weight) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.weight = weight;
    }
}