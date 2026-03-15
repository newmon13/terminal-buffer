package org.example;

public class Cursor {

    private int x;
    private int xBoundary;
    private int y;
    private int yBoundary;


    public Cursor(int xBoundary, int yBoundary) {
        this.x = 0;
        this.xBoundary = xBoundary;
        this.y = 0;
        this.yBoundary = yBoundary;
    }

    public void moveUp(int n) {
        if (this.y + n <= yBoundary) {
            this.y = this.y + n;
        }
    }

    public void moveDown(int n) {
        if (this.y - n >= 0) {
            this.y = this.y - n;
        }
    }

    public void moveRight(int n) {
        if (this.x + n <= xBoundary) {
            this.x = this.x + n;
        }
    }

    public void moveLeft(int n) {
        if (this.x - n >= 0) {
            this.x = this.x - n;
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
