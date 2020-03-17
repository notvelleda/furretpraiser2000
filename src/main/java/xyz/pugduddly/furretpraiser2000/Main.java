package xyz.pugduddly.furretpraiser2000;

import javax.swing.*;

import javax.imageio.ImageIO;

import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.*;

import club.minnced.discord.rpc.*;

public class Main extends JFrame implements ActionListener {
    private static final String title = "Furret Praiser 2000";
    private static final String subtitle = "for all your Furret praising needs";
    private static final String pleasewait = "Please wait...";
    private static final int width = 640;
    private static final int height = 400;

    private Font titleFont;

    private static final Color blurple = new Color(0x7289da);
    private static final Color white = new Color(0xffffff);
    private static final Color gray = new Color(0x99aab5);
    private static final Color darkgray = new Color(0x2c2f33);
    private static final Color darkergray = new Color(0x23272a);

    private JLabel msg;
    private JLabel msgShadow;
    private JLabel msg2;
    private JLabel msg2Shadow;

    private int numPraising = 0;
    private int numPraises = 0;
    private String userId = "";
    
    public Main() {
        System.out.println("it's furret praising time!");

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }

        /*try {
            InputStream is = getClass().getResourceAsStream("/uni-sans.heavy-caps.ttf");
            this.titleFont = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch(Exception e) {
            e.printStackTrace();
        }*/

        JPanel content = new JPanel();
        content.setBounds(0, 0, width, height);
        content.setPreferredSize(new Dimension(width, height));
        content.setBackground(darkergray);
        content.setLayout(null);
        
        JLabel text = new JLabel(title);
        if (this.titleFont != null)
            text.setFont(this.titleFont);
        text.setBounds(5, 0, width - 10, 48);
        text.setForeground(blurple);
        makeLabelBig(text, Font.BOLD, 0);
        content.add(text);
        
        JLabel textShadow = new JLabel(title);
        textShadow.setFont(text.getFont());
        textShadow.setBounds(5 + 5, 5, width - 10, 48);
        textShadow.setForeground(darkgray);
        makeLabelBig(textShadow, Font.BOLD, 0);
        content.add(textShadow);

        JLabel text2 = new JLabel(subtitle);
        text2.setBounds(25, 50, width - 10, 24);
        text2.setForeground(blurple);
        makeLabelBig(text2, Font.ITALIC, 6);
        content.add(text2);

        msg = new JLabel("Initializing RPC...");
        msg.setBounds(5, 100, this.width - 10, 24);
        msg.setForeground(white);
        makeLabelBig(msg, Font.PLAIN, 6);
        content.add(msg);

        msgShadow = new JLabel("Initializing RPC...");
        msgShadow.setBounds(6, 101, this.width - 10, 24);
        msgShadow.setForeground(darkgray);
        makeLabelBig(msgShadow, Font.PLAIN, 6);
        content.add(msgShadow);

        msg2 = new JLabel("");
        msg2.setBounds(5, 124, this.width - 10, 24);
        msg2.setForeground(white);
        makeLabelBig(msg2, Font.PLAIN, 6);
        content.add(msg2);

        msg2Shadow = new JLabel("");
        msg2Shadow.setBounds(6, 125, this.width - 10, 24);
        msg2Shadow.setForeground(darkgray);
        makeLabelBig(msg2Shadow, Font.PLAIN, 6);
        content.add(msg2Shadow);

        JButton praise = new JButton("Praise Furret");  
        praise.setBounds(25, 175, 128, 32);  
        praise.addActionListener(this);
        praise.setVisible(false);
        content.add(praise);

        try {
            BufferedImage sitt = ImageIO.read(getClass().getResource("/sitt.png"));
            JLabel sittLabel = new JLabel(new ImageIcon(sitt));
            sittLabel.setBounds(this.width - 512, 0, this.width, this.height);
            content.add(sittLabel);
            setIconImage(sitt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        add(content);
        pack();

        setTitle(this.title);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DiscordRPC lib = DiscordRPC.INSTANCE;
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        DiscordRichPresence presence = new DiscordRichPresence();

        handlers.ready = (user) -> {
            this.userId = user.userId;

            //System.out.println("Got user " + user.username + "#" + user.discriminator);
            msg.setText("Contacting server...");
            msgShadow.setText("Contacting server...");

            try {
                getURL("http://furret-praiser-2000.glitch.me/startpraising?id=" + user.userId);
            } catch (Exception e) {
                e.printStackTrace();
                msg.setText("Error connecting to server");
                msgShadow.setText("Error connecting to server");
            }

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    try {
                        getURL("http://furret-praiser-2000.glitch.me/stoppraising?id=" + user.userId);
                    } catch (Exception e) { 
                        e.printStackTrace();
                    }
                }
            }));

            praise.setVisible(true);
            updatePraises();
        };

        lib.Discord_Initialize("601483823490007042", handlers, true, "");
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.state = "Praising Furret";
        presence.details = "Praising Furret";
        presence.largeImageKey = "162furret";
        presence.largeImageText = "yay furret";
        presence.partyId = "furretParty";
        presence.partySize = 1;
        presence.partyMax = 162;
        /*presence.spectateSecret = "furretSpectate";
        presence.joinSecret = "furretJoin";*/
        lib.Discord_UpdatePresence(presence);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (praise.isVisible()) {
                    updatePraises();
                }
                
                presence.partySize = numPraising;
                lib.Discord_UpdatePresence(presence);

                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
    }
    
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand() == "Praise Furret") {
            try {
                getURL("http://furret-praiser-2000.glitch.me/praise?id=" + this.userId);
                numPraises ++;
                msg2.setText("Furret has been praised " + numPraises + " times today.");
                msg2Shadow.setText("Furret has been praised " + numPraises + " times today.");
            } catch (Exception e) {
                e.printStackTrace();
                msg.setText("Error connecting to server");
                msgShadow.setText("Error connecting to server");
                msg2.setText("");
                msg2Shadow.setText("");
            }
        }
    }

    private void updatePraises() {
        System.out.println("Updating praises...");
        String _praising = "";
        String _praises = "";
        try {
            _praising = getURL("http://furret-praiser-2000.glitch.me/praisers?id=" + this.userId);
            _praises = getURL("http://furret-praiser-2000.glitch.me/praises?id=" + this.userId);
            numPraising = Integer.parseInt(_praising);
            numPraises = Integer.parseInt(_praises);
            
            if (numPraising == 1) {
                msg.setText(numPraising + " person is praising Furret.");
                msgShadow.setText(numPraising + " person is praising Furret.");
            } else {
                msg.setText(numPraising + " people are praising Furret.");
                msgShadow.setText(numPraising + " people are praising Furret.");
            }

            msg2.setText("Furret has been praised " + numPraises + " times today.");
            msg2Shadow.setText("Furret has been praised " + numPraises + " times today.");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            if (_praising.equals(_praises)) {
                _praises = "";
            }
            msg.setText(_praising);
            msgShadow.setText(_praising);
            msg2.setText(_praises);
            msg2Shadow.setText(_praises);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setText("Error connecting to server");
            msgShadow.setText("Error connecting to server");
            msg2.setText("");
            msg2Shadow.setText("");
        }
    }

    private static void makeLabelBig(JLabel label, int flags, int bottomMargin) {
        Font labelFont = label.getFont();
        String labelText = label.getText();

        int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
        int componentWidth = label.getWidth();

        // Find out how much the font can grow in width.
        double widthRatio = (double)componentWidth / (double)stringWidth;

        int newFontSize = (int)(labelFont.getSize() * widthRatio);
        int componentHeight = label.getHeight() - bottomMargin;

        // Pick a new font size so it will not be larger than the height of label.
        int fontSizeToUse = Math.min(newFontSize, componentHeight);

        // Set the label's font size to the newly determined size.
        label.setFont(new Font(labelFont.getName(), flags, fontSizeToUse));
    }

    private static String getURL(String string) throws java.net.MalformedURLException, java.io.IOException, java.net.ProtocolException {
        HttpURLConnection con = (HttpURLConnection) new URL(string).openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", title);

        //int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public static void main(String[] args) {
        new Main();
    }
}
