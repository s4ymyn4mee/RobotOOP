package org.robotoop.gui;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Target {
    protected static volatile int positionX;
    protected static volatile int positionY;
    protected static volatile boolean created = false;

    public static int getPositionX() {
        return positionX;
    }

    public static int getPositionY() {
        return positionY;
    }

    protected static void setTargetPosition(Point p) {
        positionX = p.x;
        positionY = p.y;
    }

    protected static void drawTarget(Graphics2D g2d) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g2d.setTransform(t);

        g2d.setColor(Color.GREEN);
        GameVisualizer.fillOval(g2d,
                getPositionX(),
                getPositionY(),
                5, 5);

        g2d.setColor(Color.BLACK);
        GameVisualizer.drawOval(g2d,
                getPositionX(),
                getPositionY(),
                5, 5);
    }
}
