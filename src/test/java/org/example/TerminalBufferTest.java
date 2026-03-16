package org.example;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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