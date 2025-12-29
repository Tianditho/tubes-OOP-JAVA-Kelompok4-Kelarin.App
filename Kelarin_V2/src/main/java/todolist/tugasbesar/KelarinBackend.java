import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.io.*;

public class KelarinBackend {

    private ArrayList<Tugas> daftarTugas = new ArrayList<>();
    private String currentUser = "guest";
    private String FILE_NAME;
    private final String USER_FILE = "current_user.txt";

    public KelarinBackend() {
        loadCurrentUser();
        FILE_NAME = "data_" + currentUser + ".txt";
        loadDariFile();
    }

    private void loadCurrentUser() {
        File file = new File(USER_FILE);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String user = br.readLine();
                if (user != null && !user.trim().isEmpty()) {
                    currentUser = user.trim();
                }
            } catch (IOException e) {
                System.err.println("Gagal load user");
            }
        }
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void tambahTugas(Tugas tugas) throws Exception {
        LocalDate hariIni = LocalDate.now();

        if (tugas.getDeadline().isBefore(hariIni)) {
            throw new Exception(
                    "Deadline tidak boleh hari yang sudah berlalu.\n"
                  + "Jangan diulangi lagi ya!"
            );
        }

        daftarTugas.add(tugas);
        simpanKeFile();
    }

    public void updateTugas(int index, Tugas tugas) throws Exception {
        if (index >= 0 && index < daftarTugas.size()) {
            LocalDate hariIni = LocalDate.now();
            
            if (tugas.getDeadline().isBefore(hariIni)) {
                throw new Exception(
                        "Deadline tidak boleh hari yang sudah berlalu.\n"
                      + "Jangan diulangi lagi ya!"
                );
            }
            
            daftarTugas.set(index, tugas);
            simpanKeFile();
        }
    }

    public void hapusTugas(int index) {
        if (index >= 0 && index < daftarTugas.size()) {
            daftarTugas.remove(index);
            simpanKeFile();
        }
    }

    public ArrayList<Tugas> getDaftarTugas() {
        return daftarTugas;
    }

    public void setSelesai(int index, boolean selesai) {
        if (index >= 0 && index < daftarTugas.size()) {
            daftarTugas.get(index).setSelesai(selesai);
            simpanKeFile();
        }
    }

    public void sortByDeadline() {
        daftarTugas.sort(Comparator.comparing(Tugas::getDeadline));
        simpanKeFile();
    }

    public void sortByPrioritas() {
        daftarTugas.sort(Comparator.comparing(Tugas::getPrioritas).reversed());
        simpanKeFile();
    }

    public ArrayList<Tugas> filterByPrioritas(Prioritas prioritas) {
        return daftarTugas.stream()
                .filter(t -> t.getPrioritas() == prioritas)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Tugas> filterByStatus(boolean selesai) {
        return daftarTugas.stream()
                .filter(t -> t.isSelesai() == selesai)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Tugas> searchTugas(String keyword) {
        String lower = keyword.toLowerCase();
        return daftarTugas.stream()
                .filter(t -> t.getJudul().toLowerCase().contains(lower) ||
                           t.getDeskripsi().toLowerCase().contains(lower))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public int getTotalTugas() {
        return daftarTugas.size();
    }

    public int getTugasSelesai() {
        return (int) daftarTugas.stream().filter(Tugas::isSelesai).count();
    }

    public int getTugasAktif() {
        return getTotalTugas() - getTugasSelesai();
    }

    public int getTugasMendesak() {
        LocalDate besok = LocalDate.now().plusDays(1);
        return (int) daftarTugas.stream()
                .filter(t -> !t.isSelesai() && 
                       (t.getDeadline().equals(LocalDate.now()) || 
                        t.getDeadline().equals(besok)))
                .count();
    }

    public int getTugasTerlambat() {
        LocalDate hariIni = LocalDate.now();
        return (int) daftarTugas.stream()
                .filter(t -> !t.isSelesai() && t.getDeadline().isBefore(hariIni))
                .count();
    }

    public void cekNotifikasi() {
    LocalDate hariIni = LocalDate.now();
    LocalDate besok = hariIni.plusDays(1);
    
    // Kelompokkan tugas berdasarkan kategori
    ArrayList<Tugas> tugasBesok = new ArrayList<>();
    ArrayList<Tugas> tugasHariIni = new ArrayList<>();
    ArrayList<Tugas> tugasTerlambat = new ArrayList<>();
    
    // Pisahkan tugas ke dalam kategori
    for (Tugas t : daftarTugas) {
        if (!t.isSelesai()) {
            if (t.getDeadline().equals(besok)) {
                tugasBesok.add(t);
            } else if (t.getDeadline().equals(hariIni)) {
                tugasHariIni.add(t);
            } else if (t.getDeadline().isBefore(hariIni)) {
                tugasTerlambat.add(t);
            }
        }
    }
    
    // Urutkan berdasarkan prioritas (TINGGI > SEDANG > RENDAH)
    Comparator<Tugas> comparator = Comparator.comparing(Tugas::getPrioritas).reversed();
    tugasBesok.sort(comparator);
    tugasHariIni.sort(comparator);
    tugasTerlambat.sort(comparator);
    
    // Tampilkan notifikasi untuk DEADLINE BESOK
    if (!tugasBesok.isEmpty()) {
        StringBuilder message = new StringBuilder();
        message.append("📅 Deadline BESOK (").append(besok.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"))).append("):\n\n");
        
        for (int i = 0; i < tugasBesok.size(); i++) {
            Tugas t = tugasBesok.get(i);
            String icon = getPrioritasIcon(t.getPrioritas());
            message.append((i + 1)).append(". ").append(icon).append(" ")
                   .append(t.getJudul())
                   .append(" [").append(t.getPrioritas()).append("]")
                   .append("\n");
        }
        
        JOptionPane.showMessageDialog(
            null,
            message.toString(),
            "⏰ Pengingat Deadline Besok (" + tugasBesok.size() + " Tugas)",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // Tampilkan notifikasi untuk DEADLINE HARI INI
    if (!tugasHariIni.isEmpty()) {
        StringBuilder message = new StringBuilder();
        message.append("⚠️ DEADLINE HARI INI (").append(hariIni.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"))).append("):\n\n");
        
        for (int i = 0; i < tugasHariIni.size(); i++) {
            Tugas t = tugasHariIni.get(i);
            String icon = getPrioritasIcon(t.getPrioritas());
            message.append((i + 1)).append(". ").append(icon).append(" ")
                   .append(t.getJudul())
                   .append(" [").append(t.getPrioritas()).append("]")
                   .append("\n");
        }
        
        JOptionPane.showMessageDialog(
            null,
            message.toString(),
            "🔥 Deadline Hari Ini (" + tugasHariIni.size() + " Tugas)",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    // Tampilkan notifikasi untuk TUGAS TERLAMBAT
    if (!tugasTerlambat.isEmpty()) {
        StringBuilder message = new StringBuilder();
        message.append("❌ Tugas yang sudah melewati deadline:\n\n");
        
        for (int i = 0; i < tugasTerlambat.size(); i++) {
            Tugas t = tugasTerlambat.get(i);
            String icon = getPrioritasIcon(t.getPrioritas());
            message.append((i + 1)).append(". ").append(icon).append(" ")
                   .append(t.getJudul())
                   .append(" [").append(t.getPrioritas()).append("]")
                   .append("\n   Deadline: ").append(t.getDeadline().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                   .append("\n");
        }
        
        message.append("\n⚠️ Jangan diulangi lagi ya!");
        
        JOptionPane.showMessageDialog(
            null,
            message.toString(),
            "⛔ Tugas Terlambat (" + tugasTerlambat.size() + " Tugas)",
            JOptionPane.WARNING_MESSAGE
        );
    }
}

// Method helper untuk icon prioritas
private String getPrioritasIcon(Prioritas prioritas) {
    switch (prioritas) {
        case TINGGI:
            return "🔴";
        case SEDANG:
            return "🟡";
        case RENDAH:
            return "🟢";
        default:
            return "⚪";
    }
}

    
public void simpanKeFile() {
    System.out.println("💾 === SIMPAN FILE ===");
    System.out.println("📁 File path: " + FILE_NAME);
    System.out.println("📊 Jumlah tugas yang akan disimpan: " + daftarTugas.size());
    
    // FORMAT TANGGAL
    java.time.format.DateTimeFormatter formatter = 
        java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
        for (Tugas t : daftarTugas) {
            String line = t.getJudul() + "|" +
                         t.getDeskripsi() + "|" +
                         t.getPrioritas() + "|" +
                         t.getDeadline().format(formatter) + "|" +  // ← FIX: Format ke DD-MM-YYYY
                         t.isSelesai();
            pw.println(line);
            System.out.println("  ✏️ Menulis: " + t.getJudul());
        }
        System.out.println("✅ File berhasil disimpan!");
    } catch (IOException e) {
        System.err.println("❌ GAGAL MENYIMPAN!");
        System.err.println("❌ Error: " + e.getMessage());
        e.printStackTrace();
    }
}

private void loadDariFile() {
    File file = new File(FILE_NAME);
    
    System.out.println("📂 === LOAD FILE ===");
    System.out.println("📁 File path: " + FILE_NAME);
    System.out.println("📄 File exists? " + file.exists());
    
    if (!file.exists()) {
        System.out.println("⚠️ File tidak ditemukan, mulai dengan data kosong");
        return;
    }
    
    System.out.println("📖 Membaca file...");
    
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        int count = 0;
        
        while ((line = br.readLine()) != null) {
            System.out.println("  📄 Baca line: " + line);
            String[] data = line.split("\\|");
            
            if (data.length >= 5) {
                Tugas t = new Tugas(
                        data[0],
                        data[1],
                        Prioritas.valueOf(data[2]),
                        data[3]
                );
                t.setSelesai(Boolean.parseBoolean(data[4]));
                daftarTugas.add(t);
                count++;
                System.out.println("  ✅ Loaded: " + data[0]);
            } else {
                System.out.println("  ⚠️ Data tidak lengkap, dilewati");
            }
        }
        
        System.out.println("✅ Berhasil load " + count + " tugas dari file");
        
    } catch (Exception e) {
        System.err.println("❌ GAGAL LOAD FILE!");
        System.err.println("❌ Error: " + e.getMessage());
        e.printStackTrace();
    }
}

    public void clearData() {
        daftarTugas.clear();
    }
}