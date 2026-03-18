package org.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;


public class TerminalBuffer {

    private final static int MAX_HEIGHT = 100;

    private List<Line> screen;
    private final ArrayDeque<Line> scrollback = new ArrayDeque<>();
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

    public void increaseScreenHeight(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        if (screen.size() + n > MAX_HEIGHT) {
            throw new IllegalArgumentException("Exceeded maximum screen height: " + MAX_HEIGHT);
        }

        height += n;

        for (int i = 0; i < n; i++) {
            if (!scrollback.isEmpty()) {
                screen.add(scrollback.pollLast());
            } else {
                screen.add(new Line(width));
            }
        }
    }

    public void decreaseScreenHeight(int n) {

        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        if (screen.size() - n < 1) {
            throw new IllegalArgumentException("Screen height must be at least 1");
        }

        height -= n;

        for (int i = 0; i < n; i++) {
            if (scrollback.size() >= scrollbackMaxSize) {
                scrollback.pop();
            }
            scrollback.add(copyLine(screen.remove(0)));
        }
    }



    public void write(String text) {
        for(int codePoint: text.codePoints().toArray()) {
            Line line = screen.get(cursor.getY());
            Cell cell = line.getCell(cursor.getX());
            cell.setCharacter(new String(Character.toChars(codePoint)));
            cell.setAttributes(currentAttributes);

            if (codePoint > 0xFFFF) {
                cursor.moveRight(1);
                Cell neighbour = screen.get(cursor.getY()).getCell(cursor.getX());
                neighbour.setBlocked(true);
            }

            cursor.moveRight(1);
        }
    }

    public void insert(String text) {
        for (int codePoint : text.codePoints().toArray()) {
            boolean isWide = codePoint > 0xFFFF;
            int cellCount = isWide ? 2 : 1;

            if (isWide && cursor.getX() == width - 1) {
                cursor.moveRightWithWrap(1);
            }

            int pos = cursor.getX() + cursor.getY() * width;

            shiftScreenRight(pos, cellCount);

            Cell cell = getCell(pos);
            cell.setCharacter(new String(Character.toChars(codePoint)));
            cell.setAttributes(currentAttributes);

            if (isWide) {
                getCell(pos + 1).setBlocked(true);
            }

            cursor.moveRightWithWrap(cellCount);
        }
    }

    private void shiftScreenRight(int from, int count) {
        int total = width * height;
        for (int i = total - 1; i >= from + count; i--) {
            copy(getCell(i - count), getCell(i));
        }
        for (int i = from; i < from + count; i++) {
            getCell(i).setCharacter(null);
            getCell(i).setAttributes(null);
            getCell(i).setBlocked(false);
        }
    }

    private Cell getCell(int pos) {
        return screen.get(pos / width).getCell(pos % width);
    }

    private void copy(Cell src, Cell dst) {
        dst.setCharacter(src.getCharacter());
        dst.setAttributes(src.getAttributes());
        dst.setBlocked(src.isBlocked());
    }

    private Line copyLine(Line src) {
        Line dst = new Line(src.getWidth());
        for (int i = 0; i < src.getWidth(); i++) {
            copy(src.getCell(i), dst.getCell(i));
        }
        return dst;
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

    public void clearScreenAndScrollback() {
        clearScreen();
        scrollback.clear();
    }

    public void fillLineWith(String character) {
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
            scrollback.add(copyLine(oldestLine));
            screen.remove(0);
        }

        screen.add(newLine);
    }



    public String getScreenContent() {
        StringBuilder builder = new StringBuilder();

        for (Line line: screen) {
            builder.append(line.toString());
        }

        return builder.toString();
    }

    public String getScreenAndScrollbackContent() {
        String screenContent = getScreenContent();

        Line[] lines = scrollback.toArray(new Line[0]);
        StringBuilder builder = new StringBuilder();

        for (Line line: lines) {
            builder.append(line.toString());
        }

        return builder + screenContent;
    }

    public ArrayDeque<Line> getScrollback() {
        return this.scrollback;
    }

    public int getScrollbackSize() {
        return scrollback.size();
    }

    public Line getLineAtScreen(int y) {
        return screen.get(y);
    }

    public Line getLineAtScrollback(int y) {
        Line[] lines = scrollback.toArray(new Line[0]);
        return copyLine(lines[y]);
    }

    public String getCharacterAtScreen(int x, int y) {
        return screen.get(y).getCell(x).getCharacter();
    }

    public String getCharacterAtScrollback(int x, int y) {
        Line[] lines = scrollback.toArray(new Line[0]);
        return lines[y].getCell(x).getCharacter();
    }

    public Attributes getAttributesAtScreen(int x, int y) {
        return screen.get(y).getCell(x).getAttributes();
    }

    public Attributes getAttributesAtScrollback(int x, int y) {
        Line[] lines = scrollback.toArray(new Line[0]);
        return lines[y].getCell(x).getAttributes();
    }

    public void setAttributes(Attributes attributes) {
        this.currentAttributes = attributes;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public int getScreenHeight() {
        return screen.size();
    }
}
