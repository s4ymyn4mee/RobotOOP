package org.robotoop.gui;

import java.awt.Rectangle;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    private volatile int m_rectangleX = 0;
    private volatile int m_rectangleY = 0;
    private volatile int m_rectangleWidth = 0;
    private volatile int m_rectangleHeight = 0;

    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX;
    private volatile int m_targetPositionY;

    private static final double maxVelocity = 1;
    private final ArrayList<Rectangle> rectangles = new ArrayList<>();
//    private static final double maxAngularVelocity = 0.001;

    public GameVisualizer() {
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    removeRectangle(e.getPoint());
                    repaint();
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) { // Левая кнопка мыши
                    setTargetPosition(e.getPoint());
                } else if (SwingUtilities.isRightMouseButton(e)) { // Правая кнопка мыши
                    setRectanglePosition(e.getPoint());
                }
                repaint();

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) { // Правая кнопка мыши
                    addRectangle();
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int width = e.getX() - m_rectangleX;
                    int height = e.getY() - m_rectangleY;
                    setRectangleSize(width, height);
                    repaint();
                }
            }
        });
        setDoubleBuffered(true);
    }

    protected void setTargetPosition(Point p) {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
    }

    protected void addRectangle() {
        rectangles.add(new Rectangle(m_rectangleX, m_rectangleY, m_rectangleWidth, m_rectangleHeight));
        m_rectangleWidth = 0;
        m_rectangleHeight = 0;
    }

    protected void removeRectangle(Point p) {
        for (Rectangle rect : rectangles) {
            if (rect.contains(p)) {
                rectangles.remove(rect);
                break;
            }
        }
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    protected void onModelUpdateEvent() {
        double distance = distance(m_targetPositionX, m_targetPositionY, m_robotPositionX, m_robotPositionY);
        if (distance < 0.5) {
            return;
        }

        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        m_robotDirection = angleToTarget;

        moveRobot(maxVelocity);
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        else if (value > max)
            return max;
        return value;
    }

    private void moveRobot(double velocity) {
        velocity = applyLimits(velocity, 0, maxVelocity);

        double newX = m_robotPositionX + velocity * Math.cos(m_robotDirection);
        double newY = m_robotPositionY + velocity * Math.sin(m_robotDirection);

        m_robotPositionX = newX;
        m_robotPositionY = newY;
    }

    private static double asNormalizedRadians(double angle) {
        angle %= 2 * Math.PI;

        if (angle < 0) {
            angle += 2 * Math.PI;
        }

        return angle;
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);
        drawRectangle(g2d, m_rectangleX, m_rectangleY, m_rectangleWidth, m_rectangleHeight);
        for (Rectangle rect : rectangles) {
            drawRectangle(g2d, rect.x, rect.y, rect.width, rect.height);
        }
    }

    private void drawRectangle(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(new Color(0, 100, 0));
        g.fillRect(x, y, width, height);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = round(m_robotPositionX);
        int robotCenterY = round(m_robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.RED);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    protected void setRectanglePosition(Point p) {
        m_rectangleX = p.x;
        m_rectangleY = p.y;
    }

    protected void setRectangleSize(int width, int height) {
        m_rectangleWidth = width;
        m_rectangleHeight = height;
    }
}
