package org.example;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;

import static org.junit.jupiter.api.Assertions.*;

public class TerminalBufferTest{

    TerminalBuffer terminalBuffer;

    @BeforeEach
    public void setUp() {
        terminalBuffer = new TerminalBuffer();
        terminalBuffer.setup(10, 4, 4);

    }

    @Test
    public void shouldWriteTextToSpecifiedLine() {
        terminalBuffer.write("Hello");

        assertEquals('H', terminalBuffer.getCharacterAtScreen(0, 0).charValue());
        assertEquals('o', terminalBuffer.getCharacterAtScreen(4, 0).charValue());
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

        Character character = terminalBuffer.getCharacterAtScreen(0, 1);
        assertEquals('d', character);
    }

    @Test
    public void shouldPushBackAndWrapExistingTextWhenInsertingNew() {
        String hello = "Hello ";
        String world = "World";
        terminalBuffer.insert(world);
        terminalBuffer.getCursor().setX(0);
        terminalBuffer.insert(hello);

        Character character = terminalBuffer.getCharacterAtScreen(0, 1);
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

        assertEquals("##########", terminalBuffer.getLineAtScreen(terminalBuffer.getCursor().getY()).toString());
    }

    @Test
    void shouldFillLineWithCharacterAndOverrideExistingContent() {
        Character character = '#';

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

        System.out.println(screenContent);

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

        assertEquals('H', terminalBuffer.getCharacterAtScrollback(0, 0).charValue());
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
        terminalBuffer.fillLineWith(null);

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
    void shouldNotSetCursorXBeyondScreenWidth() {
        terminalBuffer.getCursor().setX(100);

        assertEquals(0, terminalBuffer.getCursor().getX());
    }

    @Test
    void shouldNotSetCursorYBeyondScreenHeight() {
        terminalBuffer.getCursor().setY(100);

        assertEquals(0, terminalBuffer.getCursor().getY());
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