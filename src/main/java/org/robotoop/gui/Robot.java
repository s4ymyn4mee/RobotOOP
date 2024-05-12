package org.robotoop.gui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Robot {
    private volatile double positionX;
    private volatile double positionY;
    private volatile double direction = 0;
    public static final double MAX_VELOCITY = 1;
    //  public static final double maxAngularVelocity = 0.001;
    public static final ArrayList<Robot> robots = new ArrayList<>();
    public static final int MAX_AMOUNT = 5;

    public Robot() {
        this.positionX = 100;
        this.positionY = 100;
        robots.add(this);
    }

    public Robot(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
        robots.add(this);
    }

    public Robot(double positionX, double positionY, double direction) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.direction = direction;
        robots.add(this);
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;

        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public void moveRobot(double velocity) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);

        double newX = positionX + velocity * Math.cos(direction);
        double newY = positionY + velocity * Math.sin(direction);

        positionX = newX;
        positionY = newY;
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        else if (value > max)
            return max;
        return value;
    }

    public static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    public static double asNormalizedRadians(double angle) {
        angle %= 2 * Math.PI;

        if (angle < 0) {
            angle += 2 * Math.PI;
        }

        return angle;
    }

    private void drawRobot(Graphics2D g2d) {
        int robotCenterX = GameVisualizer.round(positionX);
        int robotCenterY = GameVisualizer.round(positionY);

        AffineTransform t = AffineTransform.getRotateInstance(
                direction,
                robotCenterX,
                robotCenterY);
        g2d.setTransform(t);

        g2d.setColor(Color.RED);
        GameVisualizer.fillOval(g2d, robotCenterX, robotCenterY, 30, 10);

        g2d.setColor(Color.BLACK);
        GameVisualizer.drawOval(g2d, robotCenterX, robotCenterY, 30, 10);

        g2d.setColor(Color.WHITE);
        GameVisualizer.fillOval(g2d, robotCenterX + 10, robotCenterY, 5, 5);

        g2d.setColor(Color.BLACK);
        GameVisualizer.drawOval(g2d, robotCenterX + 10, robotCenterY, 5, 5);
    }

    public static void drawAllRobots(Graphics2D g2d) {
        for (Robot robot : robots) {
            robot.drawRobot(g2d);
        }
    }
}