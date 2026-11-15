package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Logger;

public class EnergyAuditService {
    private static final Logger LOG = Logger.getLogger(EnergyAuditService.class.getName());
    private final EnergyAuditDAO dao = new EnergyAuditDAO();
    private final ObjectItemDAO objectItemDAO = new ObjectItemDAO();

    private void validate(EnergyAudit audit) throws SQLException {
        if (audit == null) {
            throw new IllegalArgumentException("Енергоаудит не задано");
        }
        if (objectItemDAO.getById(audit.getObjectId()) == null) {
            throw new IllegalArgumentException("Об'єкт з ID " + audit.getObjectId() + " не існує");
        }
        ObjectItemService.requireText(audit.getAuditDate(), "Дата аудиту", 20);
        try {
            LocalDate.parse(audit.getAuditDate().trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Дата аудиту повинна мати формат РРРР-ММ-ДД");
        }
        ObjectItemService.requireText(audit.getAuditor(), "Аудитор", 120);
        if (audit.getNotes() != null && audit.getNotes().length() > 500) {
            throw new IllegalArgumentException("Примітки не можуть бути довшими за 500 символів");
        }
    }

    public List<EnergyAudit> getAll() throws SQLException {
        return dao.getAll();
    }

    public EnergyAudit getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(EnergyAudit audit) throws SQLException {
        validate(audit);
        dao.add(audit);
        LOG.info("Успішно додано енергоаудит для об'єкта ID " + audit.getObjectId());
    }

    public void update(EnergyAudit audit) throws SQLException {
        validate(audit);
        if (dao.getById(audit.getId()) == null) {
            throw new IllegalArgumentException("Енергоаудит з ID " + audit.getId() + " не знайдено");
        }
        dao.update(audit);
        LOG.info("Успішно оновлено енергоаудит (ID: " + audit.getId() + ")");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Успішно видалено енергоаудит (ID: " + id + ")");
    }
}
