import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import com.toedter.calendar.JDateChooser;
import java.util.Date;

public class KelarinGUI extends JFrame {

    private KelarinBackend backend = new KelarinBackend();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtJudul, txtSearch;
    private JDateChooser dateChooser;
    private JTextArea txtDeskripsi;
    private JComboBox<Prioritas> cbPrioritas;
    private JComboBox<String> cbFilterPrioritas, cbFilterStatus;

    private JButton btnTambah, btnHapus, btnEdit, btnSimpan, btnSortDeadline, btnSortPrioritas, btnClear, btnLogout;
    private int editIndex = -1;
    private JLabel lblStatus, lblUserWelcome;
    
    private JLabel lblTotalTugas, lblTugasSelesai, lblTugasAktif, lblTugasMendesak;
    private JProgressBar progressBar;
    
    private Image logoImage;

    private Font fontTitle;
    private Font fontSub;
    private Font fontForm;
    private Font fontButton;
    private Font fontLabel;
    private Font fontEmoji;
    

    private final Color PRIMARY = Color.decode("#6366F1");
    private final Color SECONDARY = Color.decode("#8B5CF6");
    private final Color ACCENT = Color.decode("#EC4899");
    private final Color SUCCESS = Color.decode("#10B981");
    private final Color WARNING = Color.decode("#F59E0B");
    private final Color DANGER = Color.decode("#EF4444");
    private final Color BG_LIGHT = Color.decode("#F9FAFB");
    private final Color CARD_BG = Color.decode("#FFFFFF");
    private final Color TEXT_DARK = Color.decode("#111827");
    private final Color TEXT_LIGHT = Color.decode("#6B7280");
    private final Color BORDER_COLOR = Color.decode("#E5E7EB");

    // TAMBAHKAN METHOD INI DI SINI (LINE 45-62)
    private Font getCJKFont(int style, int size) {
        String[] fonts = {
            "Malgun Gothic",     // Korean (Windows)
            "Microsoft YaHei",   // Chinese (Windows)
            "MS Gothic",         // Japanese (Windows)
            "Yu Gothic",         // Japanese (Windows 10+)
            "Arial Unicode MS",  // Fallback
            "Dialog"            // Java default
        };
        
        for (String fontName : fonts) {
            Font font = new Font(fontName, style, size);
            if (font.canDisplayUpTo("こんにちは안녕하세요") == -1) {
                return font;
            }
        }
        return new Font("SansSerif", style, size);
    }

    public KelarinGUI() {
    fontTitle = getCJKFont(Font.BOLD, 42);
        fontSub = getCJKFont(Font.PLAIN, 16);
        fontForm = getCJKFont(Font.PLAIN, 14);
        fontButton = getCJKFont(Font.BOLD, 13);
        fontLabel = getCJKFont(Font.BOLD, 12);
        fontEmoji = new Font("Segoe UI Emoji", Font.BOLD, 13);
        
        setTitle("KELARIN - Task Manager Pro");
        setSize(1600, 900);
    

    loadLogo();
    
    addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            confirmExit();
        }
    });

    JPanel headerPanel = buildHeader();
    JPanel dashboardPanel = buildDashboard();
    
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(headerPanel, BorderLayout.NORTH);
    topPanel.add(dashboardPanel, BorderLayout.CENTER);

add(topPanel, BorderLayout.NORTH);    
    buildContent();     
    addActions();
    refreshTable();
    
    setVisible(true);
}
    
    private void loadLogo() {
        try {
            logoImage = ImageIO.read(new File("C:\\Users\\septi\\Documents\\NetBeansProjects\\TugasBesar\\src\\main\\java\\todolist\\tugasbesar\\Kelarin.png"));
            setIconImage(logoImage);
            System.out.println("✅ Logo loaded successfully");
        } catch (Exception e) {
            System.err.println("⚠️ Logo tidak ditemukan");
        }
    }

    private void confirmExit() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "💾 Data Anda sudah tersimpan otomatis!\n\nApakah Anda yakin ingin keluar dari KELARIN?",
            "Konfirmasi Keluar",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            backend.simpanKeFile();
            dispose();
            System.exit(0);
        }
    }
    
private String getRandomGreeting(String username) {
    String[][] greetings = {
        {"[ID]", "Halo", "Selamat datang kembali"},  // Indonesia
        {"[US]", "Hello", "Welcome back"},  // USA
        {"[JP]", "こんにちは", "おかえりなさい"},  // Japan
        {"[KR]", "안녕하세요", "다시 오신 것을 환영합니다"},  // Korea
        {"[ES]", "Hola", "Bienvenido de nuevo"},  // Spain
        {"[DE]", "Hallo", "Willkommen zurück"}  // Germany
    };
    
    int random = new java.util.Random().nextInt(greetings.length);
    String[] selected = greetings[random];
    
    return selected[0] + " " + selected[1] + ", " + username + "! " + selected[2];
}

    private JPanel buildHeader() {
    JPanel header = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), 0, SECONDARY);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    header.setPreferredSize(new Dimension(0, 85));  // 110 → 85 (LEBIH PENDEK)
    header.setLayout(new BorderLayout());
    header.setBorder(new EmptyBorder(18, 40, 18, 40));  // PADDING LEBIH KECIL

    // LEFT PANEL
    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
    leftPanel.setOpaque(false);

    if (logoImage != null) {
        JLabel lblLogoImage = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 50;  // 60 → 50 (LEBIH KECIL)
                g2d.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, size, size, 10, 10));
                g2d.drawImage(logoImage, 0, 0, size, size, this);
            }
        };
        lblLogoImage.setPreferredSize(new Dimension(50, 50));
        leftPanel.add(lblLogoImage);
    }

    JLabel lblLogo = new JLabel("🚀 Yuk Kelarin Tugasmu!");
    lblLogo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));  // 28 → 24 (LEBIH KECIL)
    lblLogo.setForeground(Color.WHITE);
    leftPanel.add(lblLogo);

    // RIGHT PANEL
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.setOpaque(false);
    rightPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

    // Welcome message di atas
    JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    welcomePanel.setOpaque(false);
    
    lblUserWelcome = new JLabel(getRandomGreeting(backend.getCurrentUser()));
    lblUserWelcome.setFont(getCJKFont(Font.PLAIN, 12));
    lblUserWelcome.setForeground(Color.WHITE);
    lblUserWelcome.setHorizontalAlignment(SwingConstants.RIGHT);
    welcomePanel.add(lblUserWelcome);

    // Logout button di atas
    JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
    logoutPanel.setOpaque(false);

    btnLogout = new JButton("🚪 Logout");
    btnLogout.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
    btnLogout.setForeground(DANGER);
    btnLogout.setBackground(Color.WHITE);
    btnLogout.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(DANGER, 2),
        new EmptyBorder(6, 16, 6, 16)  // PADDING LEBIH KECIL
    ));
    btnLogout.setFocusPainted(false);
    btnLogout.setBorderPainted(true);
    btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btnLogout.setOpaque(true);
    btnLogout.setContentAreaFilled(true);
    
    btnLogout.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            btnLogout.setBackground(DANGER);
            btnLogout.setForeground(Color.WHITE);
        }
        @Override
        public void mouseExited(MouseEvent e) {
            btnLogout.setBackground(Color.WHITE);
            btnLogout.setForeground(DANGER);
        }
    });
    
    btnLogout.addActionListener(e -> logout());
    logoutPanel.add(btnLogout);

    rightPanel.add(welcomePanel, BorderLayout.NORTH);
    rightPanel.add(logoutPanel, BorderLayout.SOUTH);

    header.add(leftPanel, BorderLayout.WEST);
    header.add(rightPanel, BorderLayout.EAST);

    return header;
}

    private JPanel buildDashboard() {
    JPanel dashboardWrapper = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), 0, SECONDARY);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    dashboardWrapper.setBorder(new EmptyBorder(12, 20, 18, 20));  // 15, 20, 15, 20 → LEBIH KECIL
    
    JPanel dashboardPanel = new JPanel(new GridLayout(1, 4, 15, 0));
    dashboardPanel.setOpaque(false);
    
    JPanel card1 = createStatCard("📊 Total Tugas", "0", PRIMARY);
    JPanel card2 = createStatCard("✅ Selesai", "0", SUCCESS);
    JPanel card3 = createStatCard("⚡ Aktif", "0", WARNING);
    JPanel card4 = createStatCard("🔥 Mendesak", "0", DANGER);

    lblTotalTugas = (JLabel) card1.getComponent(1);
    lblTugasSelesai = (JLabel) card2.getComponent(1);
    lblTugasAktif = (JLabel) card3.getComponent(1);
    lblTugasMendesak = (JLabel) card4.getComponent(1);

    dashboardPanel.add(card1);
    dashboardPanel.add(card2);
    dashboardPanel.add(card3);
    dashboardPanel.add(card4);
    
    dashboardWrapper.add(dashboardPanel, BorderLayout.CENTER);

    return dashboardWrapper;
}

    private JPanel createStatCard(String title, String value, Color color) {
    JPanel card = new JPanel(new GridLayout(2, 1, 0, 3));  // 5 → 3 (SPACING LEBIH KECIL)
    card.setBackground(CARD_BG);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 1),
        new EmptyBorder(12, 15, 12, 15)  // 15, 15, 15, 15 → LEBIH KECIL
    ));

    JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
    lblTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));  // 14 → 13
    lblTitle.setForeground(TEXT_LIGHT);

    JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
    lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));  // 32 → 28
    lblValue.setForeground(color);

    card.add(lblTitle);
    card.add(lblValue);

    return card;
}

    private void buildContent() {
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(BG_LIGHT);
        formWrapper.setBorder(new EmptyBorder(5, 20, 20, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(25, 20, 25, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        JLabel formTitle = new JLabel("📝 Form Tugas");
        formTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        formTitle.setForeground(PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(formTitle, gbc);

        JSeparator sep = new JSeparator();
        gbc.gridy = 1; gbc.insets = new Insets(5, 0, 12, 0);
        formPanel.add(sep, gbc);
        gbc.insets = new Insets(6, 0, 6, 0);

        gbc.gridy = 2;
        formPanel.add(createFormLabel("✏️ Judul Tugas"), gbc);
        gbc.gridy = 3;
        txtJudul = createStyledTextField("Masukkan judul tugas...");
        formPanel.add(txtJudul, gbc);

        gbc.gridy = 4;
        formPanel.add(createFormLabel("📄 Deskripsi"), gbc);
        gbc.gridy = 5;
        txtDeskripsi = new JTextArea(3, 20);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setFont(fontForm);
        txtDeskripsi.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane descScroll = new JScrollPane(txtDeskripsi);
        descScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        descScroll.setPreferredSize(new Dimension(0, 75));
        formPanel.add(descScroll, gbc);

        gbc.gridy = 6;
        formPanel.add(createFormLabel("🎯 Prioritas"), gbc);
        gbc.gridy = 7;
        cbPrioritas = new JComboBox<>(Prioritas.values());
        cbPrioritas.setFont(fontForm);
        cbPrioritas.setBackground(Color.WHITE);
        cbPrioritas.setPreferredSize(new Dimension(0, 38));
        cbPrioritas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    Prioritas p = (Prioritas) value;
                    String icon = p == Prioritas.TINGGI ? "🔴" : p == Prioritas.SEDANG ? "🟡" : "🟢";
                    setText(icon + " " + value.toString());
                    setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
                }
                return this;
            }
        });
        formPanel.add(cbPrioritas, gbc);

        gbc.gridy = 8;
formPanel.add(createFormLabel("📅 Deadline"), gbc);
gbc.gridy = 9;
dateChooser = new JDateChooser();
dateChooser.setDateFormatString("dd-MM-yyyy");
dateChooser.setFont(fontForm);
dateChooser.setPreferredSize(new Dimension(0, 38));
dateChooser.setMinSelectableDate(new Date()); // Tidak bisa pilih tanggal lampau
formPanel.add(dateChooser, gbc);

        gbc.gridy = 10; gbc.insets = new Insets(15, 0, 6, 0);
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);

        btnTambah = createModernButton("➕ Tambah", PRIMARY);
        btnEdit = createModernButton("✏️ Edit", WARNING);
        btnSimpan = createModernButton("💾 Simpan", SUCCESS);
        btnHapus = createModernButton("🗑️ Hapus", DANGER);

        btnPanel.add(btnTambah);
        btnPanel.add(btnEdit);
        btnPanel.add(btnSimpan);
        btnPanel.add(btnHapus);
        formPanel.add(btnPanel, gbc);

        gbc.gridy = 11; gbc.insets = new Insets(6, 0, 0, 0);
        btnClear = createModernButton("🧹 Bersihkan Form", TEXT_LIGHT);
        formPanel.add(btnClear, gbc);

        formWrapper.add(formPanel, BorderLayout.CENTER);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(BG_LIGHT);
        tableWrapper.setBorder(new EmptyBorder(5, 10, 20, 20));

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(CARD_BG);
        tableHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel tableTitle = new JLabel("📋 Daftar Tugas");
        tableTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        tableTitle.setForeground(PRIMARY);

        JPanel filterSortPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        filterSortPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("🔍 Cari:");
        searchLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        
        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchTugas();
            }
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(txtSearch);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setOpaque(false);

        JLabel filterLabel = new JLabel("🔽 Filter:");
        filterLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        cbFilterStatus = new JComboBox<>(new String[]{"Semua", "Aktif", "Selesai"});
        cbFilterStatus.setFont(getCJKFont(Font.PLAIN, 12));
        cbFilterStatus.addActionListener(e -> filterTugas());

        cbFilterPrioritas = new JComboBox<>(new String[]{"Semua Prioritas", "TINGGI", "SEDANG", "RENDAH"});
        cbFilterPrioritas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFilterPrioritas.addActionListener(e -> filterTugas());

        btnSortDeadline = createSortButton("📅 Deadline");
        btnSortPrioritas = createSortButton("🎯 Prioritas");

        controlsPanel.add(filterLabel);
        controlsPanel.add(cbFilterStatus);
        controlsPanel.add(cbFilterPrioritas);
        controlsPanel.add(btnSortDeadline);
        controlsPanel.add(btnSortPrioritas);

        filterSortPanel.add(searchPanel);
        filterSortPanel.add(controlsPanel);

        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableHeader.add(filterSortPanel, BorderLayout.EAST);
        model = new DefaultTableModel(new Object[]{"Judul", "Deskripsi", "Prioritas", "Deadline", "Status"}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 4 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(fontForm);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(199, 210, 254));
        table.setSelectionForeground(TEXT_DARK);

        table.setDefaultRenderer(Object.class, new ModernTableCellRenderer());

        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(TEXT_DARK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.getViewport().setBackground(Color.WHITE);

        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        tableWrapper.add(tableCard, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(CARD_BG);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(10, 20, 10, 20)
        ));

        lblStatus = new JLabel("✨ Siap untuk produktif!");
        lblStatus.setFont(new Font("Segoe UI Emoji", Font.ITALIC, 12));
        lblStatus.setForeground(TEXT_LIGHT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setStringPainted(true);
        progressBar.setForeground(SUCCESS);

        statusBar.add(lblStatus, BorderLayout.WEST);
        statusBar.add(progressBar, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formWrapper, tableWrapper);
        split.setDividerLocation(450);
        split.setResizeWeight(0.25);
        split.setContinuousLayout(true);
        split.setDividerSize(8);
        split.setBorder(null);

        add(split, BorderLayout.CENTER);

        table.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 4 && row >= 0) {
                Boolean isSelesai = (Boolean) model.getValueAt(row, col);
                
                if (isSelesai) {
                    String judulTugas = (String) model.getValueAt(row, 0);
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "🎉 Selamat! Tugas selesai!\n\nTugas: " + judulTugas + "\n\nApakah ingin menghapus tugas ini dari daftar?",
                        "Konfirmasi Hapus Tugas Selesai",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        backend.hapusTugas(row);
                        backend.simpanKeFile();
                        refreshTable();
                        updateStatus("✅ Tugas selesai dihapus dari daftar!");
                    } else {
                        backend.setSelesai(row, true);
                        backend.simpanKeFile();
                        refreshTable();
                        updateStatus("✅ Tugas ditandai selesai!");
                    }
                } else {
                    backend.setSelesai(row, false);
                    backend.simpanKeFile();
                    refreshTable();
                    updateStatus("🔄 Status tugas diperbarui!");
                }
            }
        });
    }

    private JLabel createFormLabel(String text) {
    JLabel label = new JLabel(text);
    label.setForeground(TEXT_DARK);
    label.setFont(fontEmoji); 
    return label;
}

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(fontForm);
        field.setPreferredSize(new Dimension(0, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        field.setText(placeholder);
        field.setForeground(TEXT_LIGHT);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2),
                    new EmptyBorder(5, 10, 5, 10)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_LIGHT);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(5, 10, 5, 10)
                ));
            }
        });
        return field;
    }

    private JButton createModernButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(fontEmoji);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(0, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        
        return btn;
    }

    private JButton createSortButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        btn.setForeground(PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY, 1),
            new EmptyBorder(5, 12, 5, 12)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(PRIMARY);
                btn.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(PRIMARY);
            }
        });
        
        return btn;
    }

    private void addActions() {
        btnTambah.addActionListener(e -> tambah());
        btnHapus.addActionListener(e -> hapusTerpilih());
        btnEdit.addActionListener(e -> editTugas());
        btnSimpan.addActionListener(e -> simpanEdit());
        btnClear.addActionListener(e -> {
            clearForm();
            updateStatus("🧹 Form dibersihkan");
        });
        btnSortDeadline.addActionListener(e -> {
            backend.sortByDeadline();
            backend.simpanKeFile();
            refreshTable();
            updateStatus("📅 Tugas diurutkan berdasarkan deadline");
        });
        btnSortPrioritas.addActionListener(e -> {
            backend.sortByPrioritas();
            backend.simpanKeFile();
            refreshTable();
            updateStatus("🎯 Tugas diurutkan berdasarkan prioritas");
        });
    }

    private void tambah() {
    try {
        String judul = txtJudul.getText();
        if (judul.equals("Masukkan judul tugas...")) judul = "";
        
        if (judul.trim().isEmpty()) {
            throw new Exception("❌ Judul tugas tidak boleh kosong!");
        }
        
        // Validasi date picker
        Date selectedDate = dateChooser.getDate();
        if (selectedDate == null) {
            throw new Exception("❌ Deadline harus dipilih!");
        }
        
        // Convert Date to String format dd-MM-yyyy
        java.time.LocalDate localDate = selectedDate.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();
        String deadline = localDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        
        Tugas t = new Tugas(judul, txtDeskripsi.getText(),
                (Prioritas) cbPrioritas.getSelectedItem(), deadline);
        backend.tambahTugas(t);
        backend.simpanKeFile();
        refreshTable();
        clearForm();
        updateStatus("✅ Tugas berhasil ditambahkan dan tersimpan!");
        JOptionPane.showMessageDialog(this, 
            "🎉 Tugas berhasil ditambahkan dan tersimpan otomatis!", 
            "Berhasil", 
            JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        updateStatus("❌ Gagal menambahkan tugas");
    }
}
    private void hapusTerpilih() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "🗑️ Yakin ingin menghapus tugas ini?", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                backend.hapusTugas(row);
                backend.simpanKeFile();
                refreshTable();
                updateStatus("✅ Tugas berhasil dihapus");
            }
        } else {
            JOptionPane.showMessageDialog(this, "⚠️ Pilih baris yang ingin dihapus!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editTugas() {
    int row = table.getSelectedRow();
    if (row >= 0) {
        editIndex = row;
        txtJudul.setText((String) model.getValueAt(row, 0));
        txtJudul.setForeground(TEXT_DARK);
        txtDeskripsi.setText((String) model.getValueAt(row, 1));
        cbPrioritas.setSelectedItem(Prioritas.valueOf(model.getValueAt(row, 2).toString()));
        
        // Parse tanggal dari table dan set ke date picker
        try {
            String dateStr = (String) model.getValueAt(row, 3);
            java.time.LocalDate localDate = java.time.LocalDate.parse(dateStr, 
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            Date date = Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            dateChooser.setDate(date);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
        
        updateStatus("✏️ Mode edit aktif - Edit tugas dan klik Simpan");
        btnSimpan.setBackground(ACCENT);
    } else {
        JOptionPane.showMessageDialog(this, "⚠️ Pilih baris yang ingin diedit!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}

   private void simpanEdit() {
    if (editIndex >= 0) {
        try {
            String judul = txtJudul.getText();
            
            if (judul.trim().isEmpty()) {
                throw new Exception("❌ Judul tugas tidak boleh kosong!");
            }
            
            // Validasi date picker
            Date selectedDate = dateChooser.getDate();
            if (selectedDate == null) {
                throw new Exception("❌ Deadline harus dipilih!");
            }
            
            // Convert Date to String
            java.time.LocalDate localDate = selectedDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
            String deadline = localDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            
            Tugas t = new Tugas(judul, txtDeskripsi.getText(),
                    (Prioritas) cbPrioritas.getSelectedItem(), deadline);
            
            backend.updateTugas(editIndex, t);
            backend.simpanKeFile();
            editIndex = -1;
            refreshTable();
            clearForm();
            updateStatus("💾 Perubahan berhasil disimpan!");
            btnSimpan.setBackground(SUCCESS);
            JOptionPane.showMessageDialog(this, 
                "💾 Perubahan berhasil disimpan!", 
                "Berhasil", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "⚠️ Tidak ada tugas yang sedang diedit!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}private void clearForm() {
    txtJudul.setText("Masukkan judul tugas...");
    txtJudul.setForeground(TEXT_LIGHT);
    txtDeskripsi.setText("");
    cbPrioritas.setSelectedIndex(0);
    dateChooser.setDate(null);  // ← Clear date picker
    editIndex = -1;
}

    

    private void searchTugas() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
        } else {
            displayFilteredTasks(backend.searchTugas(keyword));
            updateStatus("🔍 Pencarian: " + keyword);
        }
    }

    private void filterTugas() {
        String status = (String) cbFilterStatus.getSelectedItem();
        String prioritas = (String) cbFilterPrioritas.getSelectedItem();
        
        java.util.ArrayList<Tugas> filtered = new java.util.ArrayList<>(backend.getDaftarTugas());
        
        if (status.equals("Aktif")) {
            filtered = backend.filterByStatus(false);
        } else if (status.equals("Selesai")) {
            filtered = backend.filterByStatus(true);
        }
        
        if (!prioritas.equals("Semua Prioritas")) {
            Prioritas p = Prioritas.valueOf(prioritas);
            java.util.ArrayList<Tugas> temp = new java.util.ArrayList<>();
            for (Tugas t : filtered) {
                if (t.getPrioritas() == p) {
                    temp.add(t);
                }
            }
            filtered = temp;
        }
        
        displayFilteredTasks(filtered);
        updateStatus("🔽 Filter diterapkan");
    }

    private void displayFilteredTasks(java.util.ArrayList<Tugas> tasks) {
        model.setRowCount(0);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (Tugas t : tasks) {
            model.addRow(new Object[]{
                    t.getJudul(), t.getDeskripsi(), t.getPrioritas(),
                    t.getDeadline().format(f), t.isSelesai()
            });
        }
        updateDashboard();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "💾 Data sudah tersimpan otomatis.\n\nApakah Anda yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            backend.simpanKeFile();
            dispose();
            SwingUtilities.invokeLater(WelcomeScreen::new);
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (Tugas t : backend.getDaftarTugas()) {
            model.addRow(new Object[]{
                    t.getJudul(), t.getDeskripsi(), t.getPrioritas(),
                    t.getDeadline().format(f), t.isSelesai()
            });
        }
        backend.cekNotifikasi();
        updateDashboard();
        
        cbFilterStatus.setSelectedIndex(0);
        cbFilterPrioritas.setSelectedIndex(0);
        txtSearch.setText("");
    }

    private void updateDashboard() {
        int total = backend.getTotalTugas();
        int selesai = backend.getTugasSelesai();
        int aktif = backend.getTugasAktif();
        int mendesak = backend.getTugasMendesak();
        
        lblTotalTugas.setText(String.valueOf(total));
        lblTugasSelesai.setText(String.valueOf(selesai));
        lblTugasAktif.setText(String.valueOf(aktif));
        lblTugasMendesak.setText(String.valueOf(mendesak));
        
        int progress = total > 0 ? (selesai * 100 / total) : 0;
        progressBar.setValue(progress);
        progressBar.setString(progress + "% Selesai");
        
        updateStatus(String.format("📊 Total: %d | ✅ Selesai: %d | ⚡ Aktif: %d | 🔥 Mendesak: %d", 
            total, selesai, aktif, mendesak));
    }

    private void updateStatus(String message) {
        lblStatus.setText(message);
    }

    class ModernTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                setForeground(TEXT_DARK);
            } else {
                setBackground(table.getSelectionBackground());
                setForeground(TEXT_DARK);
            }
            
            setBorder(new EmptyBorder(5, 10, 5, 10));
            setFont(fontForm);
            
            if (column == 2 && value != null) {
                try {
                    Prioritas p = Prioritas.valueOf(value.toString());
                    String icon = p == Prioritas.TINGGI ? "🔴" : p == Prioritas.SEDANG ? "🟡" : "🟢";
                    setText(icon + " " + value.toString());
                    if (!isSelected) {
                        Color c = p == Prioritas.TINGGI ? DANGER : p == Prioritas.SEDANG ? WARNING : SUCCESS;
                        setForeground(c);
                    }
                    setFont(getCJKFont(Font.BOLD, 12));
                } catch (Exception e) {
                    // Ignore
                }
            }
            
            return this;
        }
    }
}