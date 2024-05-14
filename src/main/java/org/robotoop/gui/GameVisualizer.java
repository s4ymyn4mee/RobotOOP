package org.robotoop.gui;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.MouseInfo;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameVisualizer extends JPanel {

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    public GameVisualizer() {
        Timer m_timer = initTimer();
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
                    CustomRectangle.removeRectangle(e.getPoint());
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (!Target.created) {
                        Target.created = true;
                    }
                    Target.setTargetPosition(e.getPoint());
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    CustomRectangle.setRectanglePosition(e.getPoint());
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    CustomRectangle.addRectangle();
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int width = e.getX() - CustomRectangle.getX();
                    int height = e.getY() - CustomRectangle.getY();
                    CustomRectangle.setRectangleSize(width, height);
                    repaint();
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                switch (keyCode) {
                    case (KeyEvent.VK_R):
                        if (Robot.robots.size() < Robot.MAX_AMOUNT) {
                            Point p = MouseInfo.getPointerInfo().getLocation();
                            SwingUtilities.convertPointFromScreen(p, GameVisualizer.this);

                            int newRobotX = p.x;
                            int newRobotY = p.y;

                            new Robot(newRobotX, newRobotY);
                            repaint();
                        }
                        break;
                    case (KeyEvent.VK_E):
                        Robot.selectNextRobot();
                        repaint();
                        break;
                }
            }
        });
        setDoubleBuffered(true);
        setFocusable(true);
        requestFocus();
    }


    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    protected void onModelUpdateEvent() {
        for (Robot robot : Robot.robots) {
            double distance = Robot.distance(Target.positionX,
                    Target.positionY,
                    robot.getPositionX(),
                    robot.getPositionY());
            if (distance < 0.5) {
                continue;
            }

            if (!robot.isTooCloseToRectangle()) {
                double angleToTarget = Robot.angleTo(robot.getPositionX(),
                        robot.getPositionY(),
                        Target.positionX,
                        Target.positionY);
                robot.setDirection(angleToTarget);
            } else {
                robot.setDirection(robot.getDirection() + Math.PI / 16);
            }
            robot.moveRobot(Robot.MAX_VELOCITY);

        }
    }

    public static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Robot.drawAllRobots(g2d);
        if (Target.created)
            Target.drawTarget(g2d);
        CustomRectangle.drawAllRectangles(g2d);
    }

    public static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    public static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
}
