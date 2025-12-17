import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Tugas {

    private String judul;
    private String deskripsi;
    private Prioritas prioritas;
    private LocalDate deadline;
    private boolean selesai;

    // Formatter tanggal
    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // ===== CONSTRUCTOR =====
    public Tugas(String judul, String deskripsi, Prioritas prioritas, String deadlineStr)
            throws DeadlineInvalidException {

        this.judul = judul;
        this.deskripsi = deskripsi;
        this.prioritas = prioritas;
        this.selesai = false;

        try {
            this.deadline = LocalDate.parse(deadlineStr, FORMAT);
        } catch (DateTimeParseException e) {
            throw new DeadlineInvalidException(
                    "Format deadline salah! Gunakan dd-MM-yyyy"
            );
        }

        // Validasi tanggal tidak boleh lewat
        if (this.deadline.isBefore(LocalDate.now())) {
            throw new DeadlineInvalidException(
                    "Deadline tidak boleh tanggal yang sudah berlalu"
            );
        }
    }

    // ===== GETTER =====
    public String getJudul() {
        return judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public Prioritas getPrioritas() {
        return prioritas;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public boolean isSelesai() {
        return selesai;
    }

    // ===== SETTER =====
    public void setSelesai(boolean selesai) {
        this.selesai = selesai;
    }

    // ===== INNER CLASS (nilai plus) =====
    public class InfoTugas {
        public String ringkasan() {
            return judul + " | " + prioritas + " | " + deadline.format(FORMAT);
        }
    }
}
