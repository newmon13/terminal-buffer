package org.example;

public class Cell {

    private Character character;
    private Attributes attributes;


    public Cell() {

    }

    public Cell(Character character) {
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "[" + character +"]";
    }
}
