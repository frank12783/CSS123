package MyApp;

import MyLibs.PlayerData;
import MyLibs.Weapon;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Frontend extends JFrame implements ActionListener {

    private PlayerData player;

    // UI Components
    private JLabel balanceLabel;
    private JLabel mpsLabel;
    private JLabel pityLabel;
    private JLabel pullResultLabel;
    private JLabel pullResultIconLabel;
    private JPanel equippedGridPanel;
    private JPanel reservesPanel;
    private JProgressBar pityProgressBar;

    // GFL2 Color Palette
    private final Color bgDark = new Color(26, 26, 26);
    private final Color panelDark = new Color(18, 18, 18);
    private final Color accentOrange = new Color(255, 102, 0);
    private final Color fgWhite = new Color(240, 240, 240);

    public Frontend() {
        player = new PlayerData();

        setTitle("Cookie Shooter: Reditus (Project Oracle)");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgDark);

        initUI();
        startGameLoop();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel topHeader = new JPanel(new GridLayout(1, 2, 10, 0));
        topHeader.setBackground(panelDark);
        topHeader.setBorder(new EmptyBorder(15, 25, 15, 25));

        balanceLabel = new JLabel("", SwingConstants.LEFT);
        balanceLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        balanceLabel.setForeground(accentOrange);

        mpsLabel = new JLabel("", SwingConstants.RIGHT);
        mpsLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mpsLabel.setForeground(fgWhite);

        updateHeaderDisplays();
        topHeader.add(balanceLabel);
        topHeader.add(mpsLabel);
        add(topHeader, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.DARK_GRAY);
        tabbedPane.setForeground(fgWhite);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));

        tabbedPane.addTab("Terminal (Operations)", createClickerPanel());
        tabbedPane.addTab("Supply Drop (Procurement)", createGachaPanel());
        tabbedPane.addTab("Armory (Loadout)", createArmoryPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createClickerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgDark);

        JButton clickButton = new JButton("ENGAGE TARGET");
        clickButton.setPreferredSize(new Dimension(300, 250));
        clickButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        clickButton.setForeground(Color.BLACK);
        clickButton.setBackground(accentOrange);
        clickButton.setFocusPainted(false);
        clickButton.setBorder(BorderFactory.createLineBorder(fgWhite, 2));

        ImageIcon terminalIcon = SpriteLoader.getSprite("terminal.png", 100, 100, "TAP", panelDark);
        clickButton.setIcon(terminalIcon);
        clickButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        clickButton.setHorizontalTextPosition(SwingConstants.CENTER);
        clickButton.setActionCommand("CLICK_MINT");
        clickButton.addActionListener(this);

        panel.add(clickButton);
        return panel;
    }

    private JPanel createGachaPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgDark);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel bannerLabel = new JLabel();
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bannerLabel.setIcon(SpriteLoader.getSprite("banner.png", 600, 150, "GFL2 TACTICAL SUPPLY DROP", panelDark));
        panel.add(bannerLabel, BorderLayout.NORTH);

        JPanel centerDisplay = new JPanel(new BorderLayout(10, 10));
        centerDisplay.setOpaque(false);

        pullResultIconLabel = new JLabel();
        pullResultIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pullResultIconLabel.setIcon(SpriteLoader.getSprite("unknown.png", 120, 120, "?", Color.DARK_GRAY));

        pullResultLabel = new JLabel("Press Pull to spend $50.00 Credits", SwingConstants.CENTER);
        pullResultLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        pullResultLabel.setForeground(fgWhite);

        centerDisplay.add(pullResultIconLabel, BorderLayout.CENTER);
        centerDisplay.add(pullResultLabel, BorderLayout.SOUTH);
        panel.add(centerDisplay, BorderLayout.CENTER);

        JPanel bottomControls = new JPanel(new GridLayout(2, 1, 10, 10));
        bottomControls.setOpaque(false);

        JButton pullButton = new JButton("PULL 1x ($50.00)");
        pullButton.setFont(new Font("SansSerif", Font.BOLD, 22));
        pullButton.setBackground(accentOrange);
        pullButton.setForeground(Color.BLACK);
        pullButton.setFocusPainted(false);
        pullButton.setActionCommand("PULL_GACHA");
        pullButton.addActionListener(this);

        JPanel pityPanel = new JPanel(new BorderLayout(10, 0));
        pityPanel.setOpaque(false);
        pityLabel = new JLabel("Pity Progress (0 / " + player.getMaxPity() + ")", SwingConstants.LEFT);
        pityLabel.setForeground(Color.LIGHT_GRAY);

        pityProgressBar = new JProgressBar(0, player.getMaxPity());
        pityProgressBar.setValue(0);
        pityProgressBar.setForeground(accentOrange);
        pityProgressBar.setBackground(Color.DARK_GRAY);

        pityPanel.add(pityLabel, BorderLayout.WEST);
        pityPanel.add(pityProgressBar, BorderLayout.CENTER);

        bottomControls.add(pullButton);
        bottomControls.add(pityPanel);
        panel.add(bottomControls, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createArmoryPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBackground(bgDark);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Half: Grid
        JPanel equippedContainer = new JPanel(new BorderLayout());
        equippedContainer.setOpaque(false);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(accentOrange), "Equipped Loadout (Max 10 Guns)");
        border.setTitleColor(accentOrange);
        equippedContainer.setBorder(border);

        equippedGridPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        equippedGridPanel.setOpaque(false);
        equippedContainer.add(equippedGridPanel, BorderLayout.CENTER);

        // Bottom Half: Horizontal Scroll
        JPanel inventoryContainer = new JPanel(new BorderLayout());
        inventoryContainer.setOpaque(false);
        TitledBorder invBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(fgWhite), "Armory Reserves (Inventory)");
        invBorder.setTitleColor(fgWhite);
        inventoryContainer.setBorder(invBorder);

        reservesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        reservesPanel.setBackground(panelDark);

        JScrollPane scrollPane = new JScrollPane(reservesPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(panelDark);
        scrollPane.setBorder(null);

        inventoryContainer.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(equippedContainer);
        mainPanel.add(inventoryContainer);
        
        refreshArmoryUI();
        return mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("CLICK_MINT")) {
            player.addCredits(player.getMoneyPerClick() + player.calculateTotalMPS());
            updateHeaderDisplays();
        } else if (cmd.equals("PULL_GACHA")) {
            if (player.spendCredits(50.0)) {
                Weapon pulled = player.pullGacha();
                updateHeaderDisplays();
                updateGachaUI(pulled);
                refreshArmoryUI();
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient Credits! Engage target to earn more.", "Tactical Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void updateHeaderDisplays() {
        balanceLabel.setText(String.format("CREDITS: $%.2f", player.getCredits()));
        mpsLabel.setText(String.format("MPS: $%.2f / sec", player.calculateTotalMPS()));
    }

    private void updateGachaUI(Weapon pulled) {
        Color rarityColor = getTacticalRarityColor(pulled.getRarity());
        pullResultLabel.setText(String.format("ACQUIRED: [%d★] %s (+%.1f MPS)", pulled.getRarity(), pulled.getName(), pulled.getEffectiveMps()));
        pullResultLabel.setForeground(rarityColor);

        ImageIcon icon = SpriteLoader.getSprite(pulled.getSpriteFileName(), 120, 120, pulled.getName(), rarityColor);
        pullResultIconLabel.setIcon(icon);

        pityLabel.setText(String.format("Pity Progress (%d / %d)", player.getPity(), player.getMaxPity()));
        pityProgressBar.setValue(player.getPity());
    }

    private void refreshArmoryUI() {
        // Update Equipped Grid
        equippedGridPanel.removeAll();
        Weapon[] equipped = player.getEquippedGuns();
        for (int i = 0; i < 10; i++) {
            Weapon w = equipped[i];
            JPanel slot = new JPanel(new BorderLayout());
            slot.setBackground(panelDark);

            if (w != null) {
                Color c = getTacticalRarityColor(w.getRarity());
                slot.setBorder(BorderFactory.createLineBorder(c, 2));

                JLabel nameLbl = new JLabel(w.getName(), SwingConstants.CENTER);
                nameLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                nameLbl.setForeground(c);

                JLabel iconLbl = new JLabel(SpriteLoader.getSprite(w.getSpriteFileName(), 60, 60, w.getName(), c));
                iconLbl.setHorizontalAlignment(SwingConstants.CENTER);

                JLabel mpsLbl = new JLabel(String.format("+%.1f/s (Ref %d)", w.getEffectiveMps(), w.getDuplicates()), SwingConstants.CENTER);
                mpsLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
                mpsLbl.setForeground(fgWhite);

                slot.add(nameLbl, BorderLayout.NORTH);
                slot.add(iconLbl, BorderLayout.CENTER);
                slot.add(mpsLbl, BorderLayout.SOUTH);
            } else {
                slot.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
                JLabel emptyLbl = new JLabel("EMPTY SLOT", SwingConstants.CENTER);
                emptyLbl.setForeground(Color.DARK_GRAY);
                slot.add(emptyLbl, BorderLayout.CENTER);
            }
            equippedGridPanel.add(slot);
        }
        equippedGridPanel.revalidate();
        equippedGridPanel.repaint();

        // Update Reserves (Horizontal Cards)
        reservesPanel.removeAll();
        for (Weapon w : player.getReserves()) {
            Color c = getTacticalRarityColor(w.getRarity());
            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setPreferredSize(new Dimension(100, 120));
            card.setBackground(bgDark);
            card.setBorder(BorderFactory.createLineBorder(c, 1));

            JLabel nameLbl = new JLabel(w.getName(), SwingConstants.CENTER);
            nameLbl.setForeground(fgWhite);
            nameLbl.setFont(new Font("SansSerif", Font.BOLD, 11));

            JLabel iconLbl = new JLabel(SpriteLoader.getSprite(w.getSpriteFileName(), 50, 50, w.getName(), c));
            iconLbl.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel dupeLbl = new JLabel("Dupes: " + w.getDuplicates(), SwingConstants.CENTER);
            dupeLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
            dupeLbl.setForeground(Color.LIGHT_GRAY);

            card.add(nameLbl, BorderLayout.NORTH);
            card.add(iconLbl, BorderLayout.CENTER);
            card.add(dupeLbl, BorderLayout.SOUTH);

            reservesPanel.add(card);
        }
        reservesPanel.revalidate();
        reservesPanel.repaint();
    }

    private Color getTacticalRarityColor(int rarity) {
        switch (rarity) {
            case 5: return accentOrange;          // SSR = Tactical Orange
            case 4: return fgWhite;               // SR = White
            default: return new Color(150, 150, 150); // R = Dark Grey
        }
    }

    private void startGameLoop() {
        Timer timer = new Timer(100, e -> {
            player.addCredits(player.calculateTotalMPS() / 10.0);
            updateHeaderDisplays();
        });
        timer.start();
    }

    // --- SPRITE LOADER INNER UTILITY ---
    public static class SpriteLoader {
        private static final String IMAGE_DIR = "images/";

        public static ImageIcon getSprite(String filename, int width, int height, String fallbackText, Color accentColor) {
            File file = new File(IMAGE_DIR + filename);
            if (file.exists()) {
                try {
                    BufferedImage originalImage = ImageIO.read(file);
                    Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImage);
                } catch (IOException e) {
                    System.err.println("Error loading image: " + filename);
                }
            }
            return new ImageIcon(createFallbackImage(width, height, fallbackText, accentColor));
        }

        private static BufferedImage createFallbackImage(int width, int height, String text, Color accentColor) {
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(26, 26, 26));
            g2.fillRect(0, 0, width, height);

            g2.setColor(accentColor);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(2, 2, width - 5, height - 5);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, width / 6)));
            FontMetrics fm = g2.getFontMetrics();
            int x = (width - fm.stringWidth(text)) / 2;
            int y = (height - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(text, Math.max(5, x), y);
            g2.dispose();
            return img;
        }
    }
}