package org.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class TerminalBuffer {

    private List<Line> screen = new ArrayList<>();
    private ArrayDeque<Line> scrollback = new ArrayDeque<>();
    private int scrollbackMaxSize;
    private int width;

    private Cursor cursor;
    private Attributes currentAttributes = new Attributes();

    public void setAttributes(Attributes attributes) {
        this.currentAttributes = attributes;
    }

    public void setup(int width, int height, int scrollbackSize) {
        this.width = width;
        this.scrollbackMaxSize = scrollbackSize;

        for (int i = 0; i < height; i++) {
            screen.add(new Line(width));
        }

        this.cursor = new Cursor(width, height);
    }

    public void write(String text) {
        for(Character character: text.toCharArray()) {
            Line line = screen.get(cursor.getY());
            Cell cell = line.getCell(cursor.getX());
            cell.setCharacter(character);
            cell.setAttributes(currentAttributes);
            cursor.moveRight(1);
        }
    }

    public String getLine(int y) {
        return screen.get(y).toString();
    }

    public Character getCharacter(int x, int y) {
        return screen.get(y).getCell(x).getCharacter();
    }

    public Attributes getAttributes(int x, int y) {
        return screen.get(y).getCell(x).getAttributes();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Line line: screen) {
            builder.append( line.toString()).append("\n");
        }

        return builder.toString();
    }

    public static void main(String[] args) {
        TerminalBuffer terminalBuffer = new TerminalBuffer();
        terminalBuffer.setup(10, 4, 10);

        System.out.println(terminalBuffer);
    }
}
