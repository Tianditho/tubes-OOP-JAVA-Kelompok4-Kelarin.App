import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;

public class KelarinGUI extends JFrame {

    private KelarinBackend backend = new KelarinBackend();
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtJudul, txtDeadline;
    private JTextArea txtDeskripsi;
    private JComboBox<Prioritas> cbPrioritas;

    private JButton btnTambah, btnHapus, btnEdit, btnSimpan, btnSortDeadline, btnSortPrioritas, btnClear;
    private int editIndex = -1;
    private JLabel lblStatus;

    // ===== FONTS =====
    private final Font fontTitle = new Font("Segoe UI", Font.BOLD, 42);
    private final Font fontSub = new Font("Segoe UI", Font.ITALIC, 16);
    private final Font fontForm = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font fontButton = new Font("Segoe UI", Font.BOLD, 13);
    private final Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);

    // ===== WARNA MODERN =====
    private final Color PRIMARY = Color.decode("#6366F1");
    private final Color PRIMARY_DARK = Color.decode("#4F46E5");
    private final Color SECONDARY = Color.decode("#8B5CF6");
    private final Color ACCENT = Color.decode("#EC4899");
    private final Color SUCCESS = Color.decode("#10B981");
    private final Color WARNING = Color.decode("#F59E0B");
    private final Color DANGER = Color.decode("#EF4444");
    private final Color BG_DARK = Color.decode("#1F2937");
    private final Color BG_LIGHT = Color.decode("#F9FAFB");
    private final Color CARD_BG = Color.decode("#FFFFFF");
    private final Color TEXT_DARK = Color.decode("#111827");
    private final Color TEXT_LIGHT = Color.decode("#6B7280");
    private final Color BORDER_COLOR = Color.decode("#E5E7EB");

    public KelarinGUI() {
        setTitle("KELARIN - Task Manager Pro");
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_LIGHT);

        buildHeader();
        buildContent();
        addActions();
        refreshTable();
        
        setVisible(true);
    }

    // ===== HEADER WITH GRADIENT =====
    private void buildHeader() {
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
        header.setPreferredSize(new Dimension(0, 120));
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel lblTitle = new JLabel("KELARIN.");
        lblTitle.setFont(fontTitle);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Kelola tugas dengan lebih efisien dan produktif");
        lblSub.setFont(fontSub);
        lblSub.setForeground(new Color(255, 255, 255, 200));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblSub);

        // Status Label
        lblStatus = new JLabel("Siap untuk produktif!");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(textPanel, BorderLayout.WEST);
        header.add(lblStatus, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    // ===== CONTENT =====
    private void buildContent() {
        // PANEL KIRI (FORM) dengan shadow effect
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(BG_LIGHT);
        formWrapper.setBorder(new EmptyBorder(20, 20, 20, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(30, 25, 30, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        // Form Title
        JLabel formTitle = new JLabel("Form Tugas");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(formTitle, gbc);

        // Separator
        JSeparator sep = new JSeparator();
        gbc.gridy = 1; gbc.insets = new Insets(5, 0, 15, 0);
        formPanel.add(sep, gbc);
        gbc.insets = new Insets(8, 0, 8, 0);

        // Judul
        gbc.gridy = 2;
        formPanel.add(createFormLabel("Judul Tugas"), gbc);
        gbc.gridy = 3;
        txtJudul = createStyledTextField("Masukkan judul tugas...");
        formPanel.add(txtJudul, gbc);

        // Deskripsi
        gbc.gridy = 4;
        formPanel.add(createFormLabel("Deskripsi"), gbc);
        gbc.gridy = 5;
        txtDeskripsi = new JTextArea(4, 20);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setFont(fontForm);
        txtDeskripsi.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane descScroll = new JScrollPane(txtDeskripsi);
        descScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        descScroll.setPreferredSize(new Dimension(0, 90));
        formPanel.add(descScroll, gbc);

        // Prioritas
        gbc.gridy = 6;
        formPanel.add(createFormLabel("Prioritas"), gbc);
        gbc.gridy = 7;
        cbPrioritas = new JComboBox<>(Prioritas.values());
        cbPrioritas.setFont(fontForm);
        cbPrioritas.setBackground(Color.WHITE);
        cbPrioritas.setPreferredSize(new Dimension(0, 40));
        cbPrioritas.setRenderer(new PrioritasRenderer());
        formPanel.add(cbPrioritas, gbc);

        // Deadline
        gbc.gridy = 8;
        formPanel.add(createFormLabel("Deadline"), gbc);
        gbc.gridy = 9;
        txtDeadline = createStyledTextField("DD-MM-YYYY");
        formPanel.add(txtDeadline, gbc);

        // TOMBOL ACTIONS
        gbc.gridy = 10; gbc.insets = new Insets(20, 0, 8, 0);
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setOpaque(false);

        btnTambah = createModernButton("+ Tambah", PRIMARY);
        btnEdit = createModernButton("Edit", WARNING);
        btnSimpan = createModernButton("Simpan", SUCCESS);
        btnHapus = createModernButton("Hapus", DANGER);

        btnPanel.add(btnTambah);
        btnPanel.add(btnEdit);
        btnPanel.add(btnSimpan);
        btnPanel.add(btnHapus);
        formPanel.add(btnPanel, gbc);

        // Clear Button
        gbc.gridy = 11; gbc.insets = new Insets(8, 0, 0, 0);
        btnClear = createModernButton("Bersihkan Form", TEXT_LIGHT);
        formPanel.add(btnClear, gbc);

        formWrapper.add(formPanel, BorderLayout.CENTER);

        // PANEL KANAN (TABEL)
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(BG_LIGHT);
        tableWrapper.setBorder(new EmptyBorder(20, 10, 20, 20));

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Table Header dengan Sort Buttons
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(CARD_BG);
        tableHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel tableTitle = new JLabel("Daftar Tugas");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tableTitle.setForeground(PRIMARY);

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        sortPanel.setOpaque(false);

        JLabel sortLabel = new JLabel("Urutkan: ");
        sortLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sortLabel.setForeground(TEXT_DARK);

        btnSortDeadline = createSortButton("By Deadline");
        btnSortPrioritas = createSortButton("By Prioritas");

        sortPanel.add(sortLabel);
        sortPanel.add(btnSortDeadline);
        sortPanel.add(btnSortPrioritas);

        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableHeader.add(sortPanel, BorderLayout.EAST);

        // Table Model
        model = new DefaultTableModel(new Object[]{"Judul", "Deskripsi", "Prioritas", "Deadline", "Kelar?"}, 0) {
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
        table.setRowHeight(45);
        table.setFont(fontForm);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(199, 210, 254)); // Light purple
        table.setSelectionForeground(TEXT_DARK); // Dark text for better contrast

        // Custom Cell Renderer
        table.setDefaultRenderer(Object.class, new ModernTableCellRenderer());

        // Column Widths
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);

        // Table Header Style
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(TEXT_DARK); // Changed to dark text
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.LEFT);
        headerRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.getViewport().setBackground(Color.WHITE);

        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        tableWrapper.add(tableCard, BorderLayout.CENTER);

        // ===== SPLIT PANE =====
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formWrapper, tableWrapper);
        split.setDividerLocation(450);
        split.setResizeWeight(0.3);
        split.setContinuousLayout(true);
        split.setDividerSize(8);
        split.setBorder(null);

        add(split, BorderLayout.CENTER);

        // Table Model Listener with Auto-Delete Confirmation
        table.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 4 && row >= 0) {
                Boolean isSelesai = (Boolean) model.getValueAt(row, col);
                
                if (isSelesai) {
                    // Tugas dicentang selesai, tampilkan konfirmasi hapus
                    String judulTugas = (String) model.getValueAt(row, 0);
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "<html><b>Selamat! Tugas selesai!</b><br><br>" +
                        "Tugas: <i>" + judulTugas + "</i><br><br>" +
                        "Apakah ingin menghapus tugas ini dari daftar?</html>",
                        "Konfirmasi Hapus Tugas Selesai",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        // Hapus tugas
                        backend.hapusTugas(row);
                        refreshTable();
                        updateStatus("Tugas selesai dihapus dari daftar!");
                    } else {
                        // Tetap tandai sebagai selesai tapi tidak hapus
                        backend.setSelesai(row, true);
                        updateStatus("Tugas ditandai selesai!");
                    }
                } else {
                    // Tugas di-uncheck
                    backend.setSelesai(row, false);
                    updateStatus("Status tugas diperbarui!");
                }
            }
        });

        // Hover effect on table
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row > -1) {
                    table.setToolTipText(getTaskTooltip(row));
                }
            }
        });
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_DARK);
        label.setFont(fontLabel);
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(fontForm);
        field.setPreferredSize(new Dimension(0, 40));
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
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_LIGHT);
                }
            }
        });
        return field;
    }

    private JButton createModernButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(fontButton);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(0, 42));
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
            @Override
            public void mousePressed(MouseEvent e) {
                btn.setBackground(bg.darker().darker());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setBackground(bg.darker());
            }
        });
        
        return btn;
    }

    private JButton createSortButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
            updateStatus("Form dibersihkan");
        });
        btnSortDeadline.addActionListener(e -> {
            backend.sortByDeadline();
            refreshTable();
            updateStatus("Tugas diurutkan berdasarkan deadline");
        });
        btnSortPrioritas.addActionListener(e -> {
            backend.sortByPrioritas();
            refreshTable();
            updateStatus("Tugas diurutkan berdasarkan prioritas");
        });
    }

    private void tambah() {
        try {
            String judul = txtJudul.getText();
            if (judul.equals("Masukkan judul tugas...")) judul = "";
            
            String deadline = txtDeadline.getText();
            if (deadline.equals("DD-MM-YYYY")) deadline = "";
            
            Tugas t = new Tugas(judul, txtDeskripsi.getText(),
                    (Prioritas) cbPrioritas.getSelectedItem(), deadline);
            backend.tambahTugas(t);
            refreshTable();
            clearForm();
            updateStatus("Tugas berhasil ditambahkan!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            updateStatus("Gagal menambahkan tugas");
        }
    }

    private void hapusTerpilih() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Yakin ingin menghapus tugas ini?", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                backend.hapusTugas(row);
                refreshTable();
                updateStatus("Tugas berhasil dihapus");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editTugas() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            editIndex = row;
            txtJudul.setText((String) model.getValueAt(row, 0));
            txtJudul.setForeground(TEXT_DARK);
            txtDeskripsi.setText((String) model.getValueAt(row, 1));
            cbPrioritas.setSelectedItem(model.getValueAt(row, 2));
            txtDeadline.setText((String) model.getValueAt(row, 3));
            txtDeadline.setForeground(TEXT_DARK);
            updateStatus("Mode edit aktif - Edit tugas dan klik Simpan");
            btnSimpan.setBackground(ACCENT);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diedit!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void simpanEdit() {
        if (editIndex >= 0) {
            try {
                Tugas t = new Tugas(txtJudul.getText(), txtDeskripsi.getText(),
                        (Prioritas) cbPrioritas.getSelectedItem(), txtDeadline.getText());
                backend.getDaftarTugas().set(editIndex, t);
                editIndex = -1;
                refreshTable();
                clearForm();
                updateStatus("Perubahan berhasil disimpan!");
                btnSimpan.setBackground(SUCCESS);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tidak ada tugas yang sedang diedit!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearForm() {
        txtJudul.setText("Masukkan judul tugas...");
        txtJudul.setForeground(TEXT_LIGHT);
        txtDeskripsi.setText("");
        cbPrioritas.setSelectedIndex(0);
        txtDeadline.setText("DD-MM-YYYY");
        txtDeadline.setForeground(TEXT_LIGHT);
        editIndex = -1;
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
        int total = backend.getDaftarTugas().size();
        long selesai = backend.getDaftarTugas().stream().filter(Tugas::isSelesai).count();
        updateStatus(String.format("Total: %d tugas | Selesai: %d | Aktif: %d", total, selesai, total - selesai));
    }

    private void updateStatus(String message) {
        lblStatus.setText(message);
    }

    private String getTaskTooltip(int row) {
        String judul = (String) model.getValueAt(row, 0);
        String desc = (String) model.getValueAt(row, 1);
        String prioritas = model.getValueAt(row, 2).toString();
        String deadline = (String) model.getValueAt(row, 3);
        boolean selesai = (Boolean) model.getValueAt(row, 4);
        String status = selesai ? "Selesai" : "Aktif";
        return "<html><b>" + judul + "</b><br>" + desc + "<br><i>" + prioritas + " | " + deadline + " | " + status + "</i></html>";
    }

    // Custom Renderer untuk Prioritas di ComboBox
    class PrioritasRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value != null) {
                Prioritas p = (Prioritas) value;
                label.setText(getPrioritasText(p));
                label.setForeground(isSelected ? Color.WHITE : getPrioritasColor(p));
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            }
            return label;
        }
    }

    // Custom Renderer untuk Table - FIXED
    class ModernTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Set text color - ALWAYS use dark text for non-selected rows
            if (!isSelected) {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                setForeground(TEXT_DARK); // DARK TEXT for unselected rows
            } else {
                // For selected rows, keep the selection background but use dark text
                setBackground(table.getSelectionBackground());
                setForeground(TEXT_DARK); // DARK TEXT for better visibility
            }
            
            setBorder(new EmptyBorder(5, 10, 5, 10));
            setFont(fontForm);
            
            // Special formatting for Prioritas column
            if (column == 2 && value != null) {
                try {
                    Prioritas p = Prioritas.valueOf(value.toString());
                    setText(getPrioritasText(p));
                    if (!isSelected) {
                        setForeground(getPrioritasColor(p));
                    }
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } catch (Exception e) {
                    // If parsing fails, just show the value
                }
            }
            
            return this;
        }
    }

    private String getPrioritasText(Prioritas p) {
        switch (p) {
            case TINGGI: return "[!] " + p.toString();
            case SEDANG: return "[~] " + p.toString();
            case RENDAH: return "[.] " + p.toString();
            default: return p.toString();
        }
    }

    private Color getPrioritasColor(Prioritas p) {
        switch (p) {
            case TINGGI: return DANGER;
            case SEDANG: return WARNING;
            case RENDAH: return SUCCESS;
            default: return TEXT_LIGHT;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(KelarinGUI::new);
    }
}
