import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;

/**
 * Welcome Screen dengan login user
 * Improved dengan background logo dan animasi
 */
public class WelcomeScreen extends JFrame {
    
    private JTextField txtUsername;
    private JButton btnLogin, btnGuest;
    private final String USER_FILE = "current_user.txt";
    
    // Modern Color Palette
    private final Color PRIMARY = Color.decode("#6366F1");
    private final Color SECONDARY = Color.decode("#8B5CF6");
    private final Color BG_LIGHT = Color.decode("#F9FAFB");
    private final Color CARD_BG = Color.decode("#FFFFFF");
    private final Color TEXT_DARK = Color.decode("#111827");
    private final Color TEXT_LIGHT = Color.decode("#6B7280");
    
    // Fonts dengan Segoe UI Emoji untuk emoji support
    private final Font EMOJI_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 20);
    private final Font EMOJI_LARGE = new Font("Segoe UI Emoji", Font.PLAIN, 60);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 60);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    
    private Image logoImage;
    private Image backgroundImage;
    
    public WelcomeScreen() {
        setTitle("KELARIN - Selamat Datang");
        setSize(700, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        loadImages();
        buildUI();
        checkAutoLogin();
        
        setVisible(true);
    }
    
    /**
     * Load images (logo dan background)
     */
    private void loadImages() {
        try {
            // Load logo untuk icon
            logoImage = ImageIO.read(new File("C:\\Users\\septi\\Documents\\NetBeansProjects\\TugasBesar\\src\\main\\java\\todolist\\tugasbesar\\Kelarin.png"));
            setIconImage(logoImage);
            
            // Load background logo (optional - bisa sama atau berbeda)
            backgroundImage = logoImage;
            
            System.out.println("✅ Logo loaded successfully");
        } catch (Exception e) {
            System.err.println("⚠️ Logo tidak ditemukan di D:\\Chrome Download\\Kelarin.png");
            System.err.println("   Aplikasi akan berjalan tanpa logo.");
        }
    }
    
    /**
     * Build UI dengan background logo
     */
    private void buildUI() {
        // Main panel dengan background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Draw background gradient
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
                                    RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(99, 102, 241, 30),
                    0, getHeight(), new Color(139, 92, 246, 30)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw background logo (watermark style)
                if (backgroundImage != null) {
                    int logoSize = 300;
                    int x = (getWidth() - logoSize) / 2;
                    int y = (getHeight() - logoSize) / 2;
                    
                    // Semi-transparent logo
                    g2d.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 0.05f));
                    g2d.drawImage(backgroundImage, x, y, logoSize, logoSize, this);
                    g2d.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 1.0f));
                }
            }
        };
        
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.setBorder(new EmptyBorder(50, 60, 50, 60));
        
        // Logo Image (rounded dengan shadow)
        if (logoImage != null) {
            JPanel logoPanel = createLogoPanel();
            mainPanel.add(logoPanel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }
        
        
        JLabel lblSubtitle = new JLabel("Task Manager Pro ✨");
        lblSubtitle.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lblSubtitle.setForeground(TEXT_LIGHT);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Login Card
        JPanel cardPanel = createLoginCard();
        
        // Footer
        JLabel lblFooter = new JLabel("📚 Kelola tugas dengan lebih efisien dan produktif");
        lblFooter.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        lblFooter.setForeground(TEXT_LIGHT);
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(lblSubtitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        mainPanel.add(cardPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(lblFooter);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Create logo panel dengan rounded corners dan shadow
     */
    private JPanel createLogoPanel() {
        JPanel logoWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (logoImage != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int size = 140;
                    int x = (getWidth() - size) / 2;
                    int y = 10;
                    
                    // Shadow
                    g2d.setColor(new Color(0, 0, 0, 40));
                    g2d.fillRoundRect(x + 5, y + 5, size, size, 35, 35);
                    
                    // Border
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(x - 2, y - 2, size + 4, size + 4, 37, 37);
                    
                    // Logo
                    g2d.setClip(new java.awt.geom.RoundRectangle2D.Float(
                        x, y, size, size, 35, 35));
                    g2d.drawImage(logoImage, x, y, size, size, this);
                }
            }
        };
        
        logoWrapper.setOpaque(false);
        logoWrapper.setPreferredSize(new Dimension(140, 160));
        logoWrapper.setMaximumSize(new Dimension(140, 160));
        logoWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        return logoWrapper;
    }
    
    /**
     * Create login card
     */
    private JPanel createLoginCard() {
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(CARD_BG);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E5E7EB"), 1, true),
            new EmptyBorder(40, 40, 40, 40)
        ));
        cardPanel.setMaximumSize(new Dimension(550, 400));
        
        // Welcome text
        JLabel lblWelcome = new JLabel("👋 Selamat Datang!");
        lblWelcome.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        lblWelcome.setForeground(TEXT_DARK);
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblInstruction = new JLabel("Masukkan username untuk melanjutkan");
        lblInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInstruction.setForeground(TEXT_LIGHT);
        lblInstruction.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Username input
        JPanel usernamePanel = createUsernamePanel();
        
        // Buttons
        btnLogin = createStyledButton("✔️ Masuk ke KELARIN", PRIMARY);
        btnLogin.addActionListener(e -> login());
        
        btnGuest = createStyledButton("👤 Masuk sebagai Guest", TEXT_LIGHT);
        btnGuest.addActionListener(e -> loginAsGuest());
        
        // Add components
        cardPanel.add(lblWelcome);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cardPanel.add(lblInstruction);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        cardPanel.add(usernamePanel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        cardPanel.add(btnLogin);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        cardPanel.add(btnGuest);
        
        return cardPanel;
    }
    
    /**
     * Create username input panel
     */
    private JPanel createUsernamePanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(450, 55));
        
        JLabel iconUser = new JLabel("👤");
        iconUser.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconUser.setBorder(new EmptyBorder(0, 5, 0, 5));
        
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E5E7EB"), 2, true),
            new EmptyBorder(12, 15, 12, 15)
        ));
        txtUsername.addActionListener(e -> login());
        
        // Focus effects
        txtUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtUsername.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2, true),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                txtUsername.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.decode("#E5E7EB"), 2, true),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
        });
        
        panel.add(iconUser, BorderLayout.WEST);
        panel.add(txtUsername, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create styled button
     */
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(450, 52));
        
        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        
        return btn;
    }
    
    /**
     * Login dengan username
     */
    private void login() {
        String username = txtUsername.getText().trim();
        
        if (username.isEmpty()) {
            showError("❌ Username tidak boleh kosong!");
            return;
        }
        
        if (username.length() < 3) {
            showError("❌ Username minimal 3 karakter!");
            return;
        }
        
        if (!username.matches("[a-zA-Z0-9_]+")) {
            showError("❌ Username hanya boleh huruf, angka, dan underscore!");
            return;
        }
        
        proceedToApp(username);
    }
    
    /**
     * Login sebagai guest
     */
    private void loginAsGuest() {
        proceedToApp("guest");
    }
    

    private void proceedToApp(String username) {
    saveCurrentUser(username);
    dispose();
    SwingUtilities.invokeLater(() -> new KelarinGUI());
}
    
    /**
     * Show error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        txtUsername.requestFocus();
    }
    
    /**
     * Save current user
     */
    private void saveCurrentUser(String username) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USER_FILE))) {
            pw.println(username);
            System.out.println("💾 User saved: " + username);
        } catch (IOException e) {
            System.err.println("⚠️ Gagal menyimpan user: " + e.getMessage());
        }
    }
    
    /**
     * Check dan load user terakhir
     */
    private void checkAutoLogin() {
        File file = new File(USER_FILE);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String savedUser = br.readLine();
                if (savedUser != null && !savedUser.trim().isEmpty()) {
                    txtUsername.setText(savedUser);
                    txtUsername.selectAll();
                    System.out.println("✅ Last user loaded: " + savedUser);
                }
            } catch (IOException e) {
                System.err.println("⚠️ Error loading last user");
            }
        }
    }
}