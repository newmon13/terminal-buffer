package org.example;

import java.util.Arrays;

public class Line {

    private final Cell[] line;


    public Line(int width) {
        this.line = new Cell[width];
        Arrays.fill(this.line, new Cell(' '));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Cell cell : line) {
            builder.append(cell.toString()).append(",");
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }
}
