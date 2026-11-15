package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Logger;

public class ConsumptionRecordService {
    private static final Logger LOG = Logger.getLogger(ConsumptionRecordService.class.getName());
    private final ConsumptionRecordDAO dao = new ConsumptionRecordDAO();
    private final ObjectItemDAO objectItemDAO = new ObjectItemDAO();

    private void validate(ConsumptionRecord record) throws SQLException {
        if (record == null) {
            throw new IllegalArgumentException("Запис споживання не задано");
        }
        if (objectItemDAO.getById(record.getObjectId()) == null) {
            throw new IllegalArgumentException("Об'єкт з ID " + record.getObjectId() + " не існує");
        }
        ObjectItemService.requireText(record.getResourceType(), "Тип ресурсу", 100);
        double v = record.getConsumptionValue();
        if (Double.isNaN(v) || Double.isInfinite(v) || v < 0) {
            throw new IllegalArgumentException("Значення споживання повинно бути невід'ємним числом");
        }
        ObjectItemService.requireText(record.getPeriod(), "Період", 20);
        try {
            YearMonth.parse(record.getPeriod().trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Період повинен мати формат РРРР-ММ");
        }
    }

    public List<ConsumptionRecord> getAll() throws SQLException {
        return dao.getAll();
    }

    public ConsumptionRecord getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(ConsumptionRecord record) throws SQLException {
        validate(record);
        dao.add(record);
        LOG.info("Успішно додано запис споживання для об'єкта ID " + record.getObjectId());
    }

    public void update(ConsumptionRecord record) throws SQLException {
        validate(record);
        if (dao.getById(record.getId()) == null) {
            throw new IllegalArgumentException("Запис споживання з ID " + record.getId() + " не знайдено");
        }
        dao.update(record);
        LOG.info("Успішно оновлено запис споживання (ID: " + record.getId() + ")");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Успішно видалено запис споживання (ID: " + id + ")");
    }
}
