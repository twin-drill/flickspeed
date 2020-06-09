package com.fs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class flickspeed {

    static double NOTIFY_SPEED = 0.3;
    static int NOTIFY_TIMER = 5;
    static String FONT_NAME = "Bahnschrift";
    static int CONVERSION;
    static String UNIT = "mph";
    static boolean PERSISTENT_OVERLAY = false;

    static double DPI;
    final static Color COLOR_BG = new Color(22, 32, 53, 255);
    final static Font DEFAULT_FONT = new Font(FONT_NAME, Font.PLAIN, 16);
    final static Font DEFAULT_FONT_SMALL = new Font(FONT_NAME, Font.PLAIN, 13);

    final static Font DEFAULT_TITLE = new Font(FONT_NAME, Font.BOLD, 22);

    public static void main(String[] args) {
        File f = new File("data.dat");
        if (f.exists() && !f.isDirectory()) {
            try {
                Scanner fileScan = new Scanner(f);
                String line;
                for (int i = 1; i < 6; i++) {
                    line = fileScan.nextLine();
                    switch (i) {
                        case 1:
                            DPI = Double.parseDouble(line);
                            break;
                        case 2:
                            NOTIFY_SPEED = Double.parseDouble(line);
                            break;
                        case 3:
                            NOTIFY_TIMER = Integer.parseInt(line);
                            break;
                        case 4:
                            CONVERSION = Integer.parseInt(line);
                            switch(CONVERSION) {
                                case 1:
                                    UNIT = " mph";
                                    break;
                                case 2:
                                    UNIT = " kph";
                                    break;
                                case 3:
                                    UNIT = " m/s";
                                    break;
                            }
                            break;
                        case 5:
                            PERSISTENT_OVERLAY = Boolean.parseBoolean(line);
                            break;
                    }
                }
                mainLoop();
            }
            catch (IOException e) {
                System.out.println("Cannot read from data.dat, is the filesystem read-only?");
            }
        }
        else { firstRun();}
    }

    public static void firstRun() {
        Path file = Paths.get("data.dat");
        JFrame firstRunFrame = new JFrame("flickspeed First Run");
        firstRunFrame.setIconImage(new ImageIcon("res/logo.png").getImage());
        firstRunFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        firstRunFrame.getContentPane().setMaximumSize(new Dimension(344, 379));
        firstRunFrame.setResizable(false);

        firstRunFrame.getContentPane().setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(0, 1));

        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 20, 100));
        panel.setBackground(COLOR_BG);
        JLabel welcome = new JLabel("Welcome to flickspeed!");
        welcome.setForeground(Color.white);
        welcome.setFont(DEFAULT_TITLE);

        JLabel insertDPI = new JLabel("Mouse DPI:");
        insertDPI.setForeground(Color.white);
        insertDPI.setFont(DEFAULT_FONT);
        JTextField dpiField = new JTextField();
        dpiField.setBackground(COLOR_BG);
        dpiField.setForeground(Color.white);
        dpiField.setFont(DEFAULT_FONT_SMALL);

        JLabel errorLabel1 = new JLabel("Please enter a valid number.");
        errorLabel1.setVisible(false);
        errorLabel1.setForeground(Color.red);
        errorLabel1.setFont(DEFAULT_FONT_SMALL);

        JLabel notifySpLabel = new JLabel("Speed Threshold:");
        notifySpLabel.setForeground(Color.white);
        notifySpLabel.setFont(DEFAULT_FONT);
        JTextField speedThreshold = new JTextField();
        speedThreshold.setBackground(COLOR_BG);
        speedThreshold.setForeground(Color.white);
        speedThreshold.setFont(DEFAULT_FONT_SMALL);

        JLabel errorLabel2 = new JLabel("Please enter a valid number.");
        errorLabel2.setVisible(false);
        errorLabel2.setForeground(Color.red);
        errorLabel2.setFont(DEFAULT_FONT_SMALL);

        JPanel microButtonPanel = new JPanel(new GridLayout(1, 0));
        microButtonPanel.setBackground(COLOR_BG);

        JRadioButton unit_MPH = new JRadioButton("mph");
        unit_MPH.setBackground(COLOR_BG);
        unit_MPH.setForeground(Color.white);
        unit_MPH.setFocusPainted(false);
        unit_MPH.setContentAreaFilled(false);
        unit_MPH.setFont(DEFAULT_FONT_SMALL);

        JRadioButton unit_KPH = new JRadioButton("kph");
        unit_KPH.setBackground(COLOR_BG);
        unit_KPH.setForeground(Color.white);
        unit_KPH.setFocusPainted(false);
        unit_KPH.setContentAreaFilled(false);
        unit_KPH.setFont(DEFAULT_FONT_SMALL);

        JRadioButton unit_MS = new JRadioButton("m/s");
        unit_MS.setBackground(COLOR_BG);
        unit_MS.setForeground(Color.white);
        unit_MS.setFocusPainted(false);
        unit_MS.setContentAreaFilled(false);
        unit_MS.setFont(DEFAULT_FONT_SMALL);

        ButtonGroup unitGroup = new ButtonGroup();
        unitGroup.add(unit_MPH);
        unitGroup.add(unit_KPH);
        unitGroup.add(unit_MS);

        JLabel notifyTimeLabel = new JLabel("Notification Time:");
        notifyTimeLabel.setFont(DEFAULT_FONT);
        notifyTimeLabel.setForeground(Color.white);
        JLabel notifyTimeLabelSub = new JLabel("(In whole seconds)");
        notifyTimeLabelSub.setFont(new Font(FONT_NAME, Font.PLAIN, 12));
        notifyTimeLabelSub.setForeground(Color.white);
        JTextField notifyTime = new JTextField();
        notifyTime.setBackground(COLOR_BG);
        notifyTime.setForeground(Color.white);
        notifyTime.setFont(DEFAULT_FONT_SMALL);

        JLabel errorLabel3 = new JLabel("Please enter a valid integer.");
        errorLabel3.setForeground(Color.red);
        errorLabel3.setFont(DEFAULT_FONT_SMALL);
        errorLabel3.setVisible(false);

        JCheckBox persistentOverlay = new JCheckBox("Enable persistent overlay on startup");
        persistentOverlay.setBackground(COLOR_BG);
        persistentOverlay.setForeground(Color.white);
        persistentOverlay.setContentAreaFilled(false);
        persistentOverlay.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        persistentOverlay.setFocusPainted(false);
        persistentOverlay.setFont(DEFAULT_FONT_SMALL);

        JPanel messageWrapper = new JPanel(new GridBagLayout());
        messageWrapper.setBackground(COLOR_BG);
        JTextArea restartMessage = new JTextArea("Due to issues with the way UI frames are handled,\nyou must restart the program after saving these values.");
        restartMessage.setEditable(false);
        restartMessage.setBackground(COLOR_BG);
        restartMessage.setForeground(Color.white);
        restartMessage.setFont(DEFAULT_FONT_SMALL);

        JButton enter = new JButton(new AbstractAction("Save and Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Path file = Paths.get("data.dat");
                String dpi = dpiField.getText();
                String spd = speedThreshold.getText();
                String time = notifyTime.getText();
                String numregex = "\\d+(\\.\\d+)?";
                if (unit_MPH.isSelected()) {
                    CONVERSION = 1;
                }
                else if (unit_KPH.isSelected()) {
                    CONVERSION = 2;
                }
                else { CONVERSION = 3;}

                if (dpi.matches(numregex)) {
                    if (spd.matches(numregex)) {
                        if (time.matches("\\d+")) {
                            List<String> lines = Arrays.asList(dpi, spd, time, String.valueOf(CONVERSION), String.valueOf(persistentOverlay.isSelected()));
                            try {
                                Files.write(file, lines);
                                firstRunFrame.dispose();
                                System.exit(0);
                            }
                            catch (IOException ioException) {
                                System.out.println("Cannot access file, is the filesystem read-only?");
                            }
                        }
                        else {
                            errorLabel3.setVisible(true);
                            errorLabel1.setVisible(false);
                            errorLabel2.setVisible(false);
                        }
                    }
                    else {
                        errorLabel2.setVisible(true);
                        errorLabel1.setVisible(false);
                    }
                }
                else { errorLabel1.setVisible(true);}
            }
        });
        enter.setFocusPainted(false);
        enter.setForeground(Color.white);
        enter.setContentAreaFilled(false);

        panel.add(welcome);

        panel.add(insertDPI);
        panel.add(dpiField);
        panel.add(errorLabel1);

        panel.add(notifySpLabel);
        panel.add(speedThreshold);
        microButtonPanel.add(unit_MPH);
        microButtonPanel.add(unit_KPH);
        microButtonPanel.add(unit_MS);
        panel.add(microButtonPanel);
        panel.add(errorLabel2);

        panel.add(notifyTimeLabel);
        panel.add(notifyTimeLabelSub);
        panel.add(notifyTime);
        panel.add(errorLabel3);

        panel.add(persistentOverlay);

        messageWrapper.add(restartMessage);
        panel.add(messageWrapper);
        panel.add(enter);
        firstRunFrame.add(panel, BorderLayout.CENTER);

        firstRunFrame.pack();
        firstRunFrame.setVisible(true);
    }

    public static void mainLoop() {
        JLabel spLb = null;
        boolean disposed = true;
        if (PERSISTENT_OVERLAY) {
            spLb = createPersistentOverlay();
            disposed = false;
        }
        boolean running = true;
        createTray();
        while (running) {
            double dots = calculateSpeed(DPI);
            double speed;
            switch(CONVERSION) {
                case 2:
                    speed = dots * (18000 / (DPI * 39370.0787));
                    break;
                case 3:
                    speed = dots * (5 / (DPI * 39.3700787));
                    break;
                case 1:
                default:
                    speed = dots * (18000 / (DPI * 63360));
                    break;
            }

            if (PERSISTENT_OVERLAY) {
                if (disposed) {
                    spLb = createPersistentOverlay();
                }
                updatePersistentOverlay(spLb, speed);
                disposed = false;
            }
            else {
                JFrame frame = (JFrame) SwingUtilities.getRoot(spLb);
                frame.dispose();
                disposed = true;
            }

            if (speed >= NOTIFY_SPEED) {
                createFrame(speed);
            }
        }
    }

    public static void updatePersistentOverlay(JLabel label, double speed) {
        String labelText = String.format("%.2f", speed);
        labelText += UNIT;
        label.setText(labelText);
    }

    public static JLabel createPersistentOverlay() {
        JFrame frame = new JFrame("flickspeed Persistent Overlay");
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        frame.setFocusableWindowState(false);
        frame.setAutoRequestFocus(false);

        frame.getRootPane().putClientProperty("apple.awt.draggableWindowBackground", false);

        JPanel panel = new JPanel();
        panel.setBackground(COLOR_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(3, 1, 1, 3));

        JLabel speedLabel = new JLabel("0.00" + UNIT);
        speedLabel.setFont(DEFAULT_FONT);
        speedLabel.setForeground(Color.white);

        panel.add(speedLabel);
        frame.add(panel);
        frame.pack();
        return speedLabel;
    }

    public static void createTray() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("res/logo.png");

            ActionListener setup = e -> firstRun();
            ActionListener persistent_toggle = e -> PERSISTENT_OVERLAY = !PERSISTENT_OVERLAY;
            ActionListener quit = e -> System.exit(0);

            PopupMenu menu = new PopupMenu();
            MenuItem setupItem = new MenuItem("Re-run setup");
            setupItem.addActionListener(setup);
            setupItem.setFont(DEFAULT_FONT_SMALL);
            MenuItem persistentItem = new MenuItem("Toggle Persistent Overlay");
            persistentItem.addActionListener(persistent_toggle);
            persistentItem.setFont(DEFAULT_FONT_SMALL);
            MenuItem quitItem = new MenuItem("Quit");
            quitItem.addActionListener(quit);
            quitItem.setFont(DEFAULT_FONT_SMALL);

            menu.add(setupItem);
            menu.add(persistentItem);
            menu.addSeparator();
            menu.add(quitItem);

            final TrayIcon icon = new TrayIcon(image, "flickspeed", menu);
            icon.setImageAutoSize(true);
            icon.setPopupMenu(menu);

            try {
                tray.add(icon);
            }
            catch (AWTException e) {
                e.printStackTrace();
            }
        }
        else {
            System.exit(2);
        }
    }

    public static void createFrame(double speed) {
        JFrame frame = new JFrame("flickspeed");
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        frame.setFocusableWindowState(false);
        frame.setAutoRequestFocus(false);

        frame.getRootPane().putClientProperty("apple.awt.draggableWindowBackground", false);

        frame.getContentPane().setLayout(new java.awt.BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        panel.setBackground(COLOR_BG);

        JLabel niceFlick = new JLabel("flick!");
        niceFlick.setFont(new Font(FONT_NAME, Font.PLAIN, 22));
        niceFlick.setForeground(Color.white);
        JLabel speedLabel;
        String speedText = String.format("<html>Speed: <FONT COLOR=orange><strong>%.2f ", speed);
        speedText = String.format(speedText + "</strong></FONT>%s</html>", UNIT);
        speedLabel = new JLabel(speedText);
        speedLabel.setFont(DEFAULT_FONT);
        speedLabel.setForeground(Color.white);
        speedLabel.setSize(speedLabel.getSize().width + 100, speedLabel.getSize().height);

        panel.add(niceFlick);
        panel.add(speedLabel);
        frame.add(panel, BorderLayout.CENTER);

        frame.pack();
        frame.transferFocusBackward();

        try {
            TimeUnit.SECONDS.sleep(NOTIFY_TIMER);
        }
        catch(InterruptedException e) { Thread.currentThread().interrupt();}

        frame.setVisible(false);
        frame.dispose();
    }

    public static double calculateSpeed(double dpi) {
        Point p = MouseInfo.getPointerInfo().getLocation();
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        }
        catch (InterruptedException e) { Thread.currentThread().interrupt();}
        Point n = MouseInfo.getPointerInfo().getLocation();

        return Math.abs(n.distance(p));
    }

}
