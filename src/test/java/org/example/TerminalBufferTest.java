package org.example;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;

import static org.junit.jupiter.api.Assertions.*;

public class TerminalBufferTest{

    TerminalBuffer terminalBuffer;

    private static final int SCREEN_WIDTH = 10;
    private static final int SCREEN_HEIGHT = 4;
    private static final int SCROLLBACK_SIZE = 4;

    @BeforeEach
    public void setUp() {
        terminalBuffer = new TerminalBuffer();
        terminalBuffer.setup(SCREEN_WIDTH, SCREEN_HEIGHT, SCROLLBACK_SIZE);

    }

    @Test
    void shouldNotThrowWhenMovingBeyondBoundaries() {
        assertDoesNotThrow(()-> {
            terminalBuffer.getCursor().moveRight(SCREEN_WIDTH + 5);
            terminalBuffer.getCursor().moveLeft(-5);
            terminalBuffer.getCursor().moveUp(-5);
            terminalBuffer.getCursor().moveDown(SCREEN_HEIGHT + 5);
            terminalBuffer.getCursor().moveRightWithWrap(SCREEN_WIDTH * SCREEN_HEIGHT);
            terminalBuffer.getCursor().moveLeftWithWrap(-SCREEN_WIDTH * SCREEN_HEIGHT);
        });

        assertEquals(SCREEN_WIDTH - 1, terminalBuffer.getCursor().getX());
        assertEquals(SCREEN_HEIGHT - 1, terminalBuffer.getCursor().getY());
    }

    @Test
    void shouldWrapRightToNextLine() {
        terminalBuffer.getCursor().moveRight(SCREEN_WIDTH - 1);
        terminalBuffer.getCursor().moveRightWithWrap(1);

        assertEquals(0, terminalBuffer.getCursor().getX());
        assertEquals(1, terminalBuffer.getCursor().getY());
    }

    @Test
    void shouldCarryRemainingStepsToNextLineWhenWrappingRight() {
        terminalBuffer.getCursor().moveRightWithWrap(SCREEN_WIDTH + 3);

        assertEquals(3, terminalBuffer.getCursor().getX());
        assertEquals(1, terminalBuffer.getCursor().getY());
    }

    @Test
    void shouldWrapLeftToPreviousLine() {
        terminalBuffer.getCursor().moveDown(1);
        terminalBuffer.getCursor().moveLeftWithWrap(1);

        assertEquals(SCREEN_WIDTH - 1, terminalBuffer.getCursor().getX());
        assertEquals(0, terminalBuffer.getCursor().getY());
    }

    @Test
    void shouldClampToOriginWhenWrappingLeftBeyondBoundary() {
        terminalBuffer.getCursor().moveRightWithWrap(5);
        terminalBuffer.getCursor().moveLeftWithWrap(10);

        assertEquals(0, terminalBuffer.getCursor().getX());
        assertEquals(0, terminalBuffer.getCursor().getY());
    }

    @Test
    void shouldClampToLastCellWhenWrappingRightBeyondBoundary() {
        terminalBuffer.getCursor().moveRightWithWrap(SCREEN_WIDTH * SCREEN_HEIGHT);

        assertEquals(SCREEN_WIDTH - 1, terminalBuffer.getCursor().getX());
        assertEquals(SCREEN_HEIGHT - 1, terminalBuffer.getCursor().getY());
    }

    @Test
    void shouldNotSetXBeyondScreenWidth() {
        terminalBuffer.getCursor().setX(SCREEN_WIDTH + 5);

        assertEquals(0, terminalBuffer.getCursor().getX());
    }

    @Test
    void shouldNotSetYBeyondScreenHeight() {
        terminalBuffer.getCursor().setY(SCREEN_HEIGHT + 5);

        assertEquals(0, terminalBuffer.getCursor().getY());
    }

    @Test
    public void shouldWriteTextToSpecifiedLine() {
        terminalBuffer.write("Hello");

        assertEquals('H', terminalBuffer.getCharacter(0, 0).charValue());
        assertEquals('o', terminalBuffer.getCharacter(4, 0).charValue());
    }

    @Test
    public void shouldReturnLineAsString() {
        String hello = "Hello";
        terminalBuffer.write(hello);

        String line = terminalBuffer.getLine(0);

        assertEquals(hello, line);
    }

    @Test
    public void shouldReturnScreenContentAsString() {
        String hello = "Hello";
        String world = "World";
        terminalBuffer.write(hello);

        terminalBuffer.getCursor().moveDown(1);
        terminalBuffer.write(world);


        String content = terminalBuffer.getScreenContent();

        assertEquals(hello + world, content);
    }

    @Test
    public void shouldInsertTextAndWrapLines() {
        String helloWorld = "Hello World";
        terminalBuffer.insert(helloWorld);

        Character character = terminalBuffer.getCharacter(0, 1);
        assertEquals('d', character);
    }

    @Test
    public void shouldPushBackAndWrapExistingTextWhenInsertingNew() {
        String hello = "Hello ";
        String world = "World";
        terminalBuffer.insert(world);
        terminalBuffer.getCursor().setX(0);
        terminalBuffer.insert(hello);

        Character character = terminalBuffer.getCharacter(0, 1);
        assertEquals('d', character);
    }


    @Test
    void shouldClearScreen() {
        terminalBuffer.insert("Hello World");
        terminalBuffer.clearScreen();
        String screenContent = terminalBuffer.getScreenContent();

        assertEquals("", screenContent);
    }

    @Test
    void shouldFillLineWithCharacter() {
        Character character = '#';

        terminalBuffer.fillLineWith(character);

        assertEquals("##########", terminalBuffer.getLine(terminalBuffer.getCursor().getY()));
    }

    @Test
    void shouldFillLineWithCharacterAndOverrideExistingContent() {
        Character character = '#';

        terminalBuffer.write("Hello World");
        terminalBuffer.fillLineWith(character);

        assertEquals("##########", terminalBuffer.getLine(terminalBuffer.getCursor().getY()));
    }

    @Test
    void shouldAddEmptyLine() {
        String text = "HelloWorld";
        terminalBuffer.write(text);
        terminalBuffer.insertEmptyLineAtTheBottomOfScreen();

        int scrollbackSize = terminalBuffer.getScrollbackSize();
        String screenContent = terminalBuffer.getScreenContent();


        assertEquals("", screenContent);
        assertEquals(1, scrollbackSize);

        ArrayDeque<Line> scrollback = terminalBuffer.getScrollback();

        assertEquals(text, scrollback.peek().toString());
    }

    @Test
    void shouldLoseContentThatExceedsScreenSizeWhenInserting() {
        terminalBuffer = new TerminalBuffer();
        terminalBuffer.setup(5,2, 0);

        String text = "Hello World";
        String expectedText = "Hello Word";

        terminalBuffer.insert(text);

        String screenContent = terminalBuffer.getScreenContent();

        assertEquals(expectedText, screenContent);
    }


    @Test
    public void shouldStampCurrentAttributesOnWrittenCells() {
        Attributes attrs = new Attributes();
        attrs.setForeground(Color.RED);
        attrs.setBackground(Color.BLUE);
        attrs.addStyle(Style.BOLD);

        terminalBuffer.setAttributes(attrs);
        terminalBuffer.write("Hi");

        Attributes cell0 = terminalBuffer.getAttributes(0, 0);
        assertEquals(Color.RED, cell0.getForeground());
        assertEquals(Color.BLUE, cell0.getBackground());
        assertTrue(cell0.getStyles().contains(Style.BOLD));
    }

    @Test
    public void shouldNotStampAttributesOnCellsWrittenBeforeAttributeChange() {
        terminalBuffer.write("A");

        Attributes attrs = new Attributes();
        attrs.setForeground(Color.GREEN);
        terminalBuffer.setAttributes(attrs);
        terminalBuffer.write("B");

        assertEquals(Color.DEFAULT, terminalBuffer.getAttributes(0, 0).getForeground());
        assertEquals(Color.GREEN, terminalBuffer.getAttributes(1, 0).getForeground());
    }
}