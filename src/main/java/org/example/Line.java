package org.example;

public class Line {

    private final Cell[] line;


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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Cell cell : line) {
            if (cell.getCharacter() != null) {
                builder.append(cell.getCharacter());
            }
        }

        return builder.toString();
    }
}
