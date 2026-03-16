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
        if (this.y - n >= 0) {
            this.y = this.y - n;
        }
    }

    public boolean moveDown(int n) {
        if (this.y + n < yBoundary) {
            this.y = this.y + n;
            return true;
        }
        return false;
    }

    public void moveRight(int n) {
        if (this.x + n < xBoundary) {
            this.x = this.x + n;
        }
    }

    public void moveRightWithWrap(int n) {
        if (this.x + n < xBoundary) {
            this.x = this.x + n;
        } else {
            if (moveDown(1)) {
                this.x = 0;
            }
        }
    }

    public void moveLeft(int n) {
        if (this.x - n >= 0) {
            this.x = this.x - n;
        }
    }

    public void moveLeftWithWrap(int n) {
        if (this.x - n >= 0) {
            this.x = this.x - n;
        } else {
            moveUp(1);
            this.x = this.xBoundary - 1;
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
