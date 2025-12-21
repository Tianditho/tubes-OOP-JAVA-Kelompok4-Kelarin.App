import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.*;


public class KelarinBackend {

    private ArrayList<Tugas> daftarTugas = new ArrayList<>();
    private final String FILE_NAME = "data_tugas.txt";

    // ================= LOAD DATA =================
    public KelarinBackend() {
        loadDariFile();
    }

    // ================= TAMBAH =================
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

    // ================= HAPUS =================
    public void hapusTugas(int index) {
        if (index >= 0 && index < daftarTugas.size()) {
            daftarTugas.remove(index);
            simpanKeFile();
        }
    }

    // ================= GET LIST =================
    public ArrayList<Tugas> getDaftarTugas() {
        return daftarTugas;
    }

    // ================= SET SELESAI =================
    public void setSelesai(int index, boolean selesai) {
        if (index >= 0 && index < daftarTugas.size()) {
            daftarTugas.get(index).setSelesai(selesai);
            simpanKeFile();
        }
    }

    // ================= SORT DEADLINE =================
    public void sortByDeadline() {
        daftarTugas.sort(Comparator.comparing(Tugas::getDeadline));
    }

    // ================= SORT PRIORITAS =================
    public void sortByPrioritas() {
        daftarTugas.sort(Comparator.comparing(Tugas::getPrioritas));
    }

    // ================= CEK NOTIFIKASI =================
    public void cekNotifikasi() {
        LocalDate hariIni = LocalDate.now();

        for (Tugas t : daftarTugas) {

            // H-1 DEADLINE
            if (!t.isSelesai()
                    && t.getDeadline().minusDays(1).equals(hariIni)) {

                JOptionPane.showMessageDialog(
                        null,
                        "⏰ Deadline BESOK!\n\nTugas: " + t.getJudul(),
                        "Pengingat Deadline",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            // DEADLINE TERLEWAT
            if (!t.isSelesai()
                    && t.getDeadline().isBefore(hariIni)) {

                JOptionPane.showMessageDialog(
                        null,
                        "⚠️ Tugas \"" + t.getJudul() + "\" sudah melewati deadline.\n"
                                + "Jangan diulangi lagi ya!",
                        "Deadline Terlewat",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    // ================= SIMPAN FILE =================
    private void simpanKeFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Tugas t : daftarTugas) {
                pw.println(
                        t.getJudul() + "|" +
                        t.getDeskripsi() + "|" +
                        t.getPrioritas() + "|" +
                        t.getDeadline() + "|" +
                        t.isSelesai()
                );
            }
        } catch (IOException e) {
            System.out.println("Gagal menyimpan data");
        }
    }

    // ================= LOAD FILE =================
    private void loadDariFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");

                Tugas t = new Tugas(
                        data[0],
                        data[1],
                        Prioritas.valueOf(data[2]),
                        data[3]
                );

                t.setSelesai(Boolean.parseBoolean(data[4]));
                daftarTugas.add(t);
            }
        } catch (Exception e) {
            System.out.println("Gagal load data");
        }
    }
}
