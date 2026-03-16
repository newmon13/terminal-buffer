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
    void shouldMoveCursorLeftWithWrap() {
        Cursor cursor = terminalBuffer.getCursor();
        cursor.setX(1);

        cursor.moveLeftWithWrap(1);

        assertEquals(0, cursor.getX());
    }

    @Test
    void shouldMoveCursorLeft() {
        Cursor cursor = terminalBuffer.getCursor();
        cursor.setX(1);

        cursor.moveLeft(1);

        assertEquals(0, cursor.getX());
    }

    @Test
    void shouldShiftEmojiToNextLineWhenNoPlaceFor2Cells() {
        String emoji = "😀";

        terminalBuffer.getCursor().setX(SCREEN_WIDTH - 1);

        terminalBuffer.insert(emoji);

        String result = terminalBuffer.getLineAtScreen(1).toString();
        assertEquals(emoji, result);
        assertTrue(terminalBuffer.getLineAtScreen(1).getCell(1).isBlocked());
    }

    @Test
    void shouldShiftLineBy2WhenInsertingEmoji() {
        String emoji = "😀";

        terminalBuffer.write("AB");
        terminalBuffer.getCursor().setX(0);
        terminalBuffer.getCursor().setY(0);
        terminalBuffer.insert(emoji);

        assertEquals(emoji, terminalBuffer.getCharacterAtScreen(0, 0));
        assertTrue(terminalBuffer.getLineAtScreen(0).getCell(1).isBlocked());
        assertEquals("A", terminalBuffer.getCharacterAtScreen(2, 0));
        assertEquals("B", terminalBuffer.getCharacterAtScreen(3, 0));
    }

    @Test
    void shouldWriteEmojiUsingWrite() {
        terminalBuffer.write("😀");

        assertEquals("😀", terminalBuffer.getCharacterAtScreen(0, 0));
        assertTrue(terminalBuffer.getLineAtScreen(0).getCell(1).isBlocked());
        assertEquals(2, terminalBuffer.getCursor().getX());
    }

    @Test
    void shouldWriteEmoji() {
        String text = "😀";

        terminalBuffer.insert(text);
        Line lineAtScreen = terminalBuffer.getLineAtScreen(terminalBuffer.getCursor().getY());

        assertEquals(text, terminalBuffer.getScreenContent());
        assertTrue(lineAtScreen.getCell(1).isBlocked());
        assertEquals(2, terminalBuffer.getCursor().getX());
    }

    @Test
    void shouldInsertEmoji() {
        String text = "Hello World";
        String emoji = "😀";

        String expectedResult = "Hello 😀World";

        terminalBuffer.insert(text);
        terminalBuffer.getCursor().setX(6);
        terminalBuffer.getCursor().setY(0);
        terminalBuffer.insert(emoji);

        Line lineAtScreen = terminalBuffer.getLineAtScreen(terminalBuffer.getCursor().getY());

        System.out.println(lineAtScreen);

        assertEquals(expectedResult, terminalBuffer.getScreenContent());
        assertTrue(lineAtScreen.getCell(7).isBlocked());
        assertEquals(8, terminalBuffer.getCursor().getX());
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

        assertEquals("H", terminalBuffer.getCharacterAtScreen(0, 0));
        assertEquals("o", terminalBuffer.getCharacterAtScreen(4, 0));
    }

    @Test
    public void shouldReturnLineAsString() {
        String hello = "Hello";
        terminalBuffer.write(hello);

        String line = terminalBuffer.getLineAtScreen(0).toString();

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

        String character = terminalBuffer.getCharacterAtScreen(0, 1);
        assertEquals("d", character);
    }

    @Test
    public void shouldPushBackAndWrapExistingTextWhenInsertingNew() {
        String hello = "Hello ";
        String world = "World";
        terminalBuffer.insert(world);
        terminalBuffer.getCursor().setX(0);
        terminalBuffer.insert(hello);

        String character = terminalBuffer.getCharacterAtScreen(0, 1);
        assertEquals("d", character);
    }


    @Test
    void shouldClearScreen() {
        terminalBuffer.insert("Hello World");
        terminalBuffer.clearScreen();

        assertEquals("", terminalBuffer.getScreenContent());
        assertEquals(0, terminalBuffer.getCursor().getX());
        assertEquals(0, terminalBuffer.getCursor().getY());
    }

    @Test
    void shouldFillLineWithCharacter() {
        String character = "#";

        terminalBuffer.fillLineWith(character);

        assertEquals("##########", terminalBuffer.getLineAtScreen(terminalBuffer.getCursor().getY()).toString());
    }

    @Test
    void shouldFillLineWithCharacterAndOverrideExistingContent() {
        String character = "#";

        terminalBuffer.write("Hello World");
        terminalBuffer.fillLineWith(character);

        assertEquals("##########", terminalBuffer.getLineAtScreen(terminalBuffer.getCursor().getY()).toString());
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
    void shouldClearScreenAndScrollback() {
        terminalBuffer.write("HelloWorld");
        terminalBuffer.insertEmptyLineAtTheBottomOfScreen();

        terminalBuffer.clearScreenAndScrollback();

        assertEquals("", terminalBuffer.getScreenContent());
        assertEquals(0, terminalBuffer.getScrollbackSize());
    }

    @Test
    void shouldReturnLineAtScrollback() {
        String text = "HelloWorld";
        terminalBuffer.write(text);
        terminalBuffer.insertEmptyLineAtTheBottomOfScreen();

        assertEquals(text, terminalBuffer.getLineAtScrollback(0).toString());
    }

    @Test
    void shouldReturnCharacterAtScrollback() {
        terminalBuffer.write("Hello");
        terminalBuffer.insertEmptyLineAtTheBottomOfScreen();

        assertEquals("H", terminalBuffer.getCharacterAtScrollback(0, 0));
    }

    @Test
    void shouldReturnAttributesAtScrollback() {
        Attributes attrs = new Attributes();
        attrs.setForeground(Color.RED);
        terminalBuffer.setAttributes(attrs);
        terminalBuffer.write("Hi");
        terminalBuffer.insertEmptyLineAtTheBottomOfScreen();

        assertEquals(Color.RED, terminalBuffer.getAttributesAtScrollback(0, 0).getForeground());
    }

    @Test
    void shouldReturnScreenAndScrollbackContent() {
        terminalBuffer.write("HelloWorld");
        terminalBuffer.insertEmptyLineAtTheBottomOfScreen();
        terminalBuffer.getCursor().setX(0);
        terminalBuffer.getCursor().setY(0);
        terminalBuffer.write("HelloWorld");

        String content = terminalBuffer.getScreenAndScrollbackContent();

        assertEquals("HelloWorldHelloWorld", content);
    }

    @Test
    void shouldFillLineWithEmptyCharacter() {
        terminalBuffer.write("HelloWorld");
        terminalBuffer.getCursor().setX(0);
        terminalBuffer.fillLineWith("");

        assertEquals("", terminalBuffer.getLineAtScreen(0).toString());
    }

    @Test
    void shouldRemoveOldestLineFromScrollbackWhenFull() {
        terminalBuffer = new TerminalBuffer();
        terminalBuffer.setup(10, 1, 2);

        terminalBuffer.write("Line1");
        terminalBuffer.insertEmptyLineAtTheBottomOfScreen();
        terminalBuffer.getCursor().setX(0);
        terminalBuffer.write("Line2");
        terminalBuffer.insertEmptyLineAtTheBottomOfScreen();
        terminalBuffer.getCursor().setX(0);
        terminalBuffer.write("Line3");
        terminalBuffer.insertEmptyLineAtTheBottomOfScreen();

        assertEquals(2, terminalBuffer.getScrollbackSize());
        assertEquals("Line2", terminalBuffer.getLineAtScrollback(0).toString());
        assertEquals("Line3", terminalBuffer.getLineAtScrollback(1).toString());
    }

    @Test
    public void shouldStampCurrentAttributesOnWrittenCells() {
        Attributes attrs = new Attributes();
        attrs.setForeground(Color.RED);
        attrs.setBackground(Color.BLUE);
        attrs.addStyle(Style.BOLD);

        terminalBuffer.setAttributes(attrs);
        terminalBuffer.write("Hi");

        Attributes cell0 = terminalBuffer.getAttributesAtScreen(0, 0);
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

        assertEquals(Color.DEFAULT, terminalBuffer.getAttributesAtScreen(0, 0).getForeground());
        assertEquals(Color.GREEN, terminalBuffer.getAttributesAtScreen(1, 0).getForeground());
    }
}