package org.example;

import java.util.ArrayDeque;

public class TerminalBuffer {

    private ArrayDeque<Line> screen = new ArrayDeque<>();
    private ArrayDeque<Line> scrollback = new ArrayDeque<>();


    public void setup(int width, int height, int scrollbackSize) {

        for (int i = 0; i < height; i++) {
            screen.add(new Line(width));
        }

        for (int i = 0; i < scrollbackSize; i++) {
            scrollback.add(new Line(width));
        }
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
