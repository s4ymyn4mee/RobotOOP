package org.robotoop.gui;

import org.robotoop.log.Logger;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Robot {
    private volatile double positionX;
    private volatile double positionY;
    private volatile double direction = 0;
    private final int index;
    protected static final double MAX_VELOCITY = 1;
    protected static final int MAX_AMOUNT = 10;
    private static final ArrayList<Robot> robots = new ArrayList<>();
    private static int selectedRobotIndex = -1;
    private static final int MARGIN = 10;

    public Robot(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.index = robots.size();
        robots.add(this);
        Logger.debug("Добавлен новый робот: x=" + positionX + ", y=" + positionY);
    }

    public static ArrayList<Robot> getRobots() {
        return robots;
    }

    public static void selectNextRobot() {
        selectedRobotIndex = (selectedRobotIndex + 1) % getRobots().size();
        Logger.debug("Выбран текущий робот с индексом " + selectedRobotIndex);
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

    public int getIndex() {
        return index;
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

        double nextX = positionX + velocity * Math.cos(direction);
        double nextY = positionY + velocity * Math.sin(direction);

        positionX = nextX;
        positionY = nextY;
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

    public boolean isTooCloseToRectangle() {
        Rectangle robotBounds = new Rectangle(GameVisualizer.round(positionX) - MARGIN,
                GameVisualizer.round(positionY) - MARGIN,
                2 * MARGIN,
                2 * MARGIN);
        for (Rectangle rect : CustomRectangle.getRectangles()) {
            if (rect.intersects(robotBounds)) {
                return true;
            }
        }
        return false;
    }

    private void drawRobot(Graphics2D g2d) {
        int robotCenterX = GameVisualizer.round(positionX);
        int robotCenterY = GameVisualizer.round(positionY);

        AffineTransform t = AffineTransform.getRotateInstance(
                direction,
                robotCenterX,
                robotCenterY);
        g2d.setTransform(t);

        if (index != selectedRobotIndex) {
            g2d.setColor(Color.RED);
        } else {
            g2d.setColor(Color.BLUE);
        }
        GameVisualizer.fillOval(g2d, robotCenterX, robotCenterY, 30, 10);

        g2d.setColor(Color.BLACK);
        GameVisualizer.drawOval(g2d, robotCenterX, robotCenterY, 30, 10);

        g2d.setColor(Color.WHITE);
        GameVisualizer.fillOval(g2d, robotCenterX + 10, robotCenterY, 5, 5);

        g2d.setColor(Color.BLACK);
        GameVisualizer.drawOval(g2d, robotCenterX + 10, robotCenterY, 5, 5);
    }

    public static void drawAllRobots(Graphics2D g2d) {
        for (Robot robot : Robot.getRobots()) {
            robot.drawRobot(g2d);
        }
    }
}