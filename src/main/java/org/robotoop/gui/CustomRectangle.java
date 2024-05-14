package org.robotoop.gui;

import org.robotoop.log.Logger;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;

public class CustomRectangle {
    private static volatile int m_rectangleX = 0;
    private static volatile int m_rectangleY = 0;
    private static volatile int m_rectangleWidth = 0;
    private static volatile int m_rectangleHeight = 0;

    public static final ArrayList<Rectangle> rectangles = new ArrayList<>();

//    public CustomRectangle(int x, int y, int width, int height) {
//        this.x = x;
//        this.y = y;
//        this.width = width;
//        this.height = height;
//    }

//    public Rectangle getRectangle() {
//        return new Rectangle(x, y, width, height);
//    }

    public static int getX() {
        return m_rectangleX;
    }

    public static int getY() {
        return m_rectangleY;
    }

    public static int getWidth() {
        return m_rectangleWidth;
    }

    public static int getHeight() {
        return m_rectangleHeight;
    }

    public static void setRectanglePosition(Point p) {
        m_rectangleX = p.x;
        m_rectangleY = p.y;
    }

    public static void setRectangleSize(int width, int height) {
        m_rectangleWidth = width;
        m_rectangleHeight = height;
    }

    public static void addRectangle() {
        if (m_rectangleHeight > 0 && m_rectangleWidth > 0) {
            rectangles.add(new Rectangle(m_rectangleX, m_rectangleY, m_rectangleWidth, m_rectangleHeight));
            m_rectangleWidth = 0;
            m_rectangleHeight = 0;
            Logger.debug("Добавлен новый прямоугольник: x=" + m_rectangleX + ", y=" + m_rectangleY);
        }
    }

    public static void removeRectangle(Point p) {
        for (Rectangle rect : rectangles) {
            if (rect.contains(p)) {
                Logger.debug("Удалён прямоугольник: x=" + rect.getX() + ", y=" + rect.getY());
                rectangles.remove(rect);
            }
        }
    }

    public static void drawRectangle(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(new Color(0, 100, 0));
        g.fillRect(x, y, width, height);
    }

    public static void drawAllRectangles(Graphics2D g2d) {
        drawRectangle(g2d,
                CustomRectangle.getX(),
                CustomRectangle.getY(),
                CustomRectangle.getWidth(),
                CustomRectangle.getHeight());

        for (Rectangle rect : rectangles) {
            CustomRectangle.drawRectangle(g2d, rect.x, rect.y, rect.width, rect.height);
        }
    }
}