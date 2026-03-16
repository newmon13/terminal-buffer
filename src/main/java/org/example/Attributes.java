package org.example;

import java.util.HashSet;
import java.util.Set;

public class Attributes {

    private Color background = Color.DEFAULT;
    private Color foreground = Color.DEFAULT;
    private final Set<Style> styles = new HashSet<>();

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public Set<Style> getStyles() {
        return styles;
    }

    public void addStyle(Style style) {
        styles.add(style);
    }

    public void removeStyle(Style style) {
        styles.remove(style);
    }
}
