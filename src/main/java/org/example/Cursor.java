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
        if (n <= 0) return;
        if (this.y - n >= 0) {
            this.y = this.y - n;
        }
    }

    public boolean moveDown(int n) {
        if (n <= 0) return false;
        if (this.y + n < yBoundary) {
            this.y = this.y + n;
            return true;
        }
        return false;
    }

    public void moveRight(int n) {
        if (n <= 0) return;
        if (this.x + n < this.xBoundary) {
            this.x = this.x + n;
        }
    }

    public void moveRightWithWrap(int n) {
        if (n <= 0) return;
        if (this.x + this.y * this.xBoundary + n >= this.xBoundary * this.yBoundary) {
            this.x = this.xBoundary - 1;
            this.y = this.yBoundary - 1;
            return;
        }

        if (this.x + n < this.xBoundary) {
            this.x = this.x + n;
        } else {
            int total = this.x + n;
            moveDown(total / this.xBoundary);
            this.x = total % this.xBoundary;
        }
    }

    public void moveLeft(int n) {
        if (n <= 0) return;
        if (this.x - n >= 0) {
            this.x = this.x - n;
        }
    }

    public void moveLeftWithWrap(int n) {
        if (n <= 0) return;
        if (this.x + this.y * this.xBoundary < n) {
            this.x = 0;
            this.y = 0;
            return;
        }

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
        if (x >= 0 && x < xBoundary) {
            this.x = x;
        }
    }

    public void setXBoundary(int xBoundary) {
        this.xBoundary = xBoundary;
        if (this.x >= xBoundary) {
            this.x = xBoundary - 1;
        }
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        if (y >= 0 && y < yBoundary) {
            this.y = y;
        }
    }
}
