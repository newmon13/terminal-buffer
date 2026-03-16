package org.example;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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