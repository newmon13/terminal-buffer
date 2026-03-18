package org.example;

import java.util.Arrays;

public class Line {

    private Cell[] line;


    public Line(int width) {
        this.line = new Cell[width];
        for (int i = 0; i < width; i++) {
            this.line[i] = new Cell();
        }
    }

    public Cell getCell(int position) {
        return line[position];
    }

    public int getWidth() {
        return line.length;
    }

    public void expand(int n) {
        Cell[] newLine = Arrays.copyOf(line, line.length + n);
        for (int i = line.length; i < newLine.length; i++) {
            newLine[i] = new Cell();
        }
        line = newLine;
    }

    public void shrink(int n) {
        line = Arrays.copyOf(line, line.length - n);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Cell cell : line) {
            if (!cell.isBlocked() && cell.getCharacter() != null) {
                builder.append(cell.getCharacter());
            }
        }

        return builder.toString();
    }
}
