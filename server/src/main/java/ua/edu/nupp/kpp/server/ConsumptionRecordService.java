package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ConsumptionRecordService {
    private static final Logger LOG = Logger.getLogger(MeasureService.class.getName());
    private final ConsumptionRecordDAO dao = new ConsumptionRecordDAO();
    private final EnergyAuditDAO auditDAO = new EnergyAuditDAO();

    public List<ConsumptionRecord> getAll() throws SQLException {
        return dao.getAll();
    }

    public ConsumptionRecord getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(ConsumptionRecord record) throws SQLException {
        dao.add(record);
        LOG.info("Успішно додано вимірювання");
    }

    public void update(ConsumptionRecord record) throws SQLException {
        dao.update(record);
        LOG.info("Успішно оновлено вимірювання");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Успішно видалено вимірювання");    }
}