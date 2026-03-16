package org.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;


public class TerminalBuffer {

    private List<Line> screen;
    private ArrayDeque<Line> scrollback = new ArrayDeque<>();
    private int scrollbackMaxSize;
    private int width;
    private int height;

    private Cursor cursor;
    private Attributes currentAttributes = new Attributes();

    public void setup(int width, int height, int scrollbackSize) {
        this.width = width;
        this.height = height;
        this.scrollbackMaxSize = scrollbackSize;
        this.screen = new ArrayList<>(width);
        this.cursor = new Cursor(width, height);

        for (int i = 0; i < height; i++) {
            screen.add(new Line(width));
        }
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

    public void insert(String text) {
        for (Character character: text.toCharArray()) {
            int row = cursor.getY();

            Cell overflow = shiftLineRight(screen.get(row), cursor.getX());
            Cell cell = screen.get(row).getCell(cursor.getX());
            cell.setCharacter(character);
            cell.setAttributes(currentAttributes);

            cursor.moveRightWithWrap(1);

            row++;
            while (overflow != null && row < screen.size()) {
                Cell oldOverflow = overflow;
                overflow = shiftLineRight(screen.get(row), 0);
                screen.get(row).getCell(0).setCharacter(oldOverflow.getCharacter());
                screen.get(row).getCell(0).setAttributes(oldOverflow.getAttributes());
                row++;
            }
        }
    }

    public void clearScreen() {
        for (Line line : screen) {
            for (int j = 0; j < line.getWidth(); j++) {
                Cell cell = line.getCell(j);
                cell.setCharacter(null);
            }
        }
        cursor.setX(0);
        cursor.setY(0);
    }

    public void fillLineWith(Character character) {
        Line line = screen.get(cursor.getY());
        for (int i = 0; i < line.getWidth(); i++) {
            line.getCell(i).setCharacter(character);
            line.getCell(i).setAttributes(currentAttributes);
        }
    }

    public void insertEmptyLineAtTheBottomOfScreen() {
        Line newLine = new Line(width);

        if (screen.size() >= height) {
            Line oldestLine = screen.get(0);

            if (scrollback.size() >= scrollbackMaxSize) {
                scrollback.pop();
            }
            scrollback.add(oldestLine);
            screen.remove(0);
        }

        screen.add(newLine);
    }

    private Cell shiftLineRight(Line line, int index) {
        Cell last = line.getCell(line.getWidth() - 1);
        Cell overflow = new Cell(last.getCharacter());
        overflow.setAttributes(last.getAttributes());

        for (int i = line.getWidth() - 1; i > index; i--) {
            Cell prev = line.getCell(i - 1);
            line.getCell(i).setCharacter(prev.getCharacter());
            line.getCell(i).setAttributes(prev.getAttributes());
        }

        return overflow.getCharacter() == null ? null : overflow;
    }


    public String getScreenContent() {
        StringBuilder builder = new StringBuilder();

        for (Line line: screen) {
            builder.append(line.toString());
        }

        return builder.toString();
    }

    public int getScreenSize() {
        return screen.size();
    }

    public ArrayDeque<Line> getScrollback() {
        return this.scrollback;
    }

    public int getScrollbackSize() {
        return scrollback.size();
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

    public void setAttributes(Attributes attributes) {
        this.currentAttributes = attributes;
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Line line: screen) {
            builder.append( line.toString()).append("\n");
        }

        return builder.toString();
    }
}
