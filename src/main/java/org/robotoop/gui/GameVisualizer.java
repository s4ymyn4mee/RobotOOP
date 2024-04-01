package org.robotoop.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;
    private double goalX; // Цель по оси X
    private double goalY; // Цель по оси Y
    private double windowWidth = 1900; // Ширина окна
    private double windowHeight = 1000; // Высота окна
    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    public static double maxVelocity = 0.1;
    public static double maxAngularVelocity = 0.001;

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
                setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setDoubleBuffered(true);
    }

    protected void setTargetPosition(Point p) {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
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
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5) {
            maxVelocity = 0.1;
            maxAngularVelocity = 0.001;
            return;
        }
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;
        if (angleToTarget > m_robotDirection) {
            angularVelocity = maxAngularVelocity;
        } else if (angleToTarget < m_robotDirection) {
            angularVelocity = -maxAngularVelocity;
        }

        moveRobot(maxVelocity, angularVelocity, 10);
    }


    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        // Перед применением любых изменений проверяем, не выходят ли новые X и Y за границы окна.
        double tentativeNewX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(tentativeNewX)) {
            tentativeNewX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double tentativeNewY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(tentativeNewY)) {
            tentativeNewY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }

        // Границы окна
        double margin = 3.0; // Задаём отступ от края окна, чтобы робот не касался границ
        double lowerBoundX = margin;
        double upperBoundX = windowWidth - margin;
        double lowerBoundY = margin;
        double upperBoundY = windowHeight - margin;

        // Проверяем, не выходят ли предполагаемые новые координаты за границы окна
        if (tentativeNewX >= lowerBoundX && tentativeNewX <= upperBoundX &&
                tentativeNewY >= lowerBoundY && tentativeNewY <= upperBoundY) {
            // Если новые координаты находятся в пределах окна, обновляем позицию робота
            m_robotPositionX = tentativeNewX;
            m_robotPositionY = tentativeNewY;
        } else {

        }

        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotDirection = newDirection;
    }

//    private void moveRobotToGoal(double duration) {
//        // Рассчитываем вектор до цели
//        double toGoalX = goalX - m_robotPositionX;
//        double toGoalY = goalY - m_robotPositionY;
//
//        // Рассчитываем угол до цели
//        double angleToGoal = Math.atan2(toGoalY, toGoalX);
//
//        // Нормализуем угол
//        double normalAngle = asNormalizedRadians(angleToGoal - m_robotDirection);
//
//        // Вычисляем угловую скорость для минимального поворота робота к цели
//        double angularVelocity = normalAngle > Math.PI ? -maxAngularVelocity : maxAngularVelocity;
//        if (Math.abs(normalAngle) < Math.toRadians(10)) { // Если робот почти повернут к цели
//            angularVelocity = 0; // Остановим поворот
//        }
//
//        // Рассчитываем новые координаты
//        double newX, newY;
//        if (angularVelocity == 0) {
//            // Если робот направлен к цели, идем прямо
//            newX = m_robotPositionX + maxVelocity * duration * Math.cos(angleToGoal);
//            newY = m_robotPositionY + maxVelocity * duration * Math.sin(angleToGoal);
//        } else {
//            newX = m_robotPositionX + maxVelocity / angularVelocity * (Math.sin(m_robotDirection + angularVelocity * duration) - Math.sin(m_robotDirection));
//            newY = m_robotPositionY - maxVelocity / angularVelocity * (Math.cos(m_robotDirection + angularVelocity * duration) - Math.cos(m_robotDirection));
//        }
//
//        // Проверяем не выходим ли за границы окна
//        if (newX < 0 || newX > windowWidth || newY < 0 || newY > windowHeight) {
//            maxVelocity = 0;
//            maxAngularVelocity = 0; // Остановим робота, если он достигает границы
//        } else {
//            // Обновляем позицию робота
//            m_robotPositionX = newX;
//            m_robotPositionY = newY;
//            m_robotDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
//        }
//    }


    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);
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
        g.setColor(Color.MAGENTA);
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
}