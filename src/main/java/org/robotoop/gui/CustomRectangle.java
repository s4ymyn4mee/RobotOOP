package org.robotoop.gui;

import org.robotoop.log.Logger;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;

public class CustomRectangle {
    private static volatile int m_currentRectangleX = 0;
    private static volatile int m_currentRectangleY = 0;
    private static volatile int m_currentRectangleWidth = 0;
    private static volatile int m_currentRectangleHeight = 0;
    private static final ArrayList<Rectangle> rectangles = new ArrayList<>();

    public static int getX() {
        return m_currentRectangleX;
    }

    public static int getY() {
        return m_currentRectangleY;
    }

    public static int getWidth() {
        return m_currentRectangleWidth;
    }

    public static int getHeight() {
        return m_currentRectangleHeight;
    }

    public static ArrayList<Rectangle> getRectangles() {
        return rectangles;
    }

    public static void setRectanglePosition(Point p) {
        m_currentRectangleX = p.x;
        m_currentRectangleY = p.y;
    }

    public static void setRectangleSize(int width, int height) {
        m_currentRectangleWidth = width;
        m_currentRectangleHeight = height;
    }

    public static void addRectangle() {
        if (m_currentRectangleHeight > 0 && m_currentRectangleWidth > 0) {
            rectangles.add(new Rectangle(m_currentRectangleX, m_currentRectangleY,
                    m_currentRectangleWidth, m_currentRectangleHeight));
            m_currentRectangleWidth = 0;
            m_currentRectangleHeight = 0;
            Logger.debug("Добавлен новый прямоугольник: x=" + m_currentRectangleX + "," +
                    " y=" + m_currentRectangleY);
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