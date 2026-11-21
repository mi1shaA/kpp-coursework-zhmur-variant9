package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportRecordsTask extends Task<Integer> {
    private static final Logger LOG = Logger.getLogger(ImportRecordsTask.class.getName());
    private static final String HEADER = "object_id;resource_type;consumption_value;period";

    private final Path path;
    private final HttpEnergyAuditClient api;

    private volatile int total;
    private volatile int imported;
    private volatile int skipped;

    public ImportRecordsTask(Path path, HttpEnergyAuditClient api) {
        this.path = path;
        this.api = api;
    }

    public int getTotal() {
        return total;
    }

    public int getImported() {
        return imported;
    }

    public int getSkipped() {
        return skipped;
    }

    @Override
    protected Integer call() throws Exception {
        String fileName = path.getFileName().toString();
        LOG.info("Початок CSV-імпорту: " + fileName);
        updateMessage("Читання файлу " + fileName);

        List<String> lines = readLines(fileName);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("CSV-файл порожній: " + fileName);
        }

        String header = lines.get(0).replace("\uFEFF", "").trim();
        if (!HEADER.equals(header)) {
            throw new IllegalArgumentException("Некоректний заголовок CSV. Очікується: " + HEADER);
        }

        int rows = 0;
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                rows++;
            }
        }
        total = rows;

        if (rows == 0) {
            updateProgress(0, 1);
            updateMessage("У файлі немає рядків для імпорту");
            LOG.info("CSV-імпорт: файл не містить рядків даних");
            return 0;
        }

        int processed = 0;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            if (isCancelled()) {
                LOG.warning("CSV-імпорт скасовано користувачем. Імпортовано: " + imported);
                updateMessage("Імпорт скасовано");
                return imported;
            }

            int lineNumber = i + 1;
            ConsumptionRecord record = parse(line, lineNumber);
            if (record == null) {
                skipped++;
            } else {
                try {
                    api.addRecord(record);
                    imported++;
                } catch (RuntimeException e) {
                    LOG.log(Level.SEVERE, "CSV-імпорт перервано на рядку " + lineNumber, e);
                    throw new IllegalStateException("Рядок " + lineNumber + ": " + e.getMessage()
                            + ". Імпортовано записів до збою: " + imported, e);
                }
            }

            processed++;
            updateProgress(processed, rows);
            updateMessage("Опрацьовано " + processed + " із " + rows);
        }

        LOG.info("CSV-імпорт завершено. Рядків: " + rows + ", імпортовано: " + imported + ", пропущено: " + skipped);
        return imported;
    }

    private List<String> readLines(String fileName) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException("Файл не знайдено: " + path + ". Перевірте, що він лежить у каталозі client/sample-data");
        }
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IOException("Не вдалося прочитати файл " + fileName + " (потрібне кодування UTF-8)", e);
        }
    }

    private ConsumptionRecord parse(String line, int lineNumber) {
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            LOG.warning("Рядок " + lineNumber + " пропущено: очікується 4 поля, знайдено " + fields.length);
            return null;
        }
        String resourceType = fields[1].trim();
        String period = fields[3].trim();
        if (resourceType.isEmpty() || period.isEmpty()) {
            LOG.warning("Рядок " + lineNumber + " пропущено: порожній тип ресурсу або період");
            return null;
        }
        try {
            long objectId = Long.parseLong(fields[0].trim());
            double value = Double.parseDouble(fields[2].trim());
            if (!Double.isFinite(value)) {
                LOG.warning("Рядок " + lineNumber + " пропущено: значення споживання не є скінченним числом");
                return null;
            }
            return new ConsumptionRecord(null, objectId, resourceType, value, period);
        } catch (NumberFormatException e) {
            LOG.warning("Рядок " + lineNumber + " пропущено: некоректне число (" + e.getMessage() + ")");
            return null;
        }
    }
}