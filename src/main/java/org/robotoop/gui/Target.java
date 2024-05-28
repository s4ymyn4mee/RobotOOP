package org.robotoop.gui;

import org.robotoop.log.Logger;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Target {
    private static volatile int positionX;
    private static volatile int positionY;
    private static volatile boolean created = false;

    public static int getPositionX() {
        return positionX;
    }

    public static int getPositionY() {
        return positionY;
    }

    public static boolean isCreated() {
        return created;
    }

    public static void setCreated(boolean created) {
        Target.created = created;
    }

    protected static void setTargetPosition(Point p) {
        positionX = p.x;
        positionY = p.y;
        Logger.debug("Таргет переставлен в точку: x=" + positionX + ", y=" + positionY);
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
