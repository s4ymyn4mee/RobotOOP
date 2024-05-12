package org.robotoop.gui;

import org.robotoop.log.Logger;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuBarCreator {

    public static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu documentMenu = createDocumentMenu();
        JMenu testMenu = createTestMenu();

        menuBar.add(documentMenu);
        menuBar.add(testMenu);

        return menuBar;
    }

    private static JMenu createDocumentMenu() {
        JMenu documentMenu = new JMenu("Document");
        documentMenu.setMnemonic(KeyEvent.VK_D);

        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.setMnemonic(KeyEvent.VK_N);
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newMenuItem.setActionCommand("new");

        JMenuItem quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.setMnemonic(KeyEvent.VK_Q);
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        quitMenuItem.setActionCommand("quit");

        documentMenu.add(newMenuItem);
        documentMenu.add(quitMenuItem);

        return documentMenu;
    }

    private static JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Tests");
        testMenu.setMnemonic(KeyEvent.VK_T);

        JMenuItem addLogMessageItem = new JMenuItem("Add Log Message");
        addLogMessageItem.setMnemonic(KeyEvent.VK_A);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("New Log Message");
        });

        testMenu.add(addLogMessageItem);

        return testMenu;
    }
}