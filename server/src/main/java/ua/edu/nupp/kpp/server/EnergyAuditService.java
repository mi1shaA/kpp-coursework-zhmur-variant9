package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class EnergyAuditService {
    private static final Logger LOG = Logger.getLogger(MeasureService.class.getName());
    private final EnergyAuditDAO dao = new EnergyAuditDAO();
    private final ObjectItemDAO objectItemDAO = new ObjectItemDAO();

    public List<EnergyAudit> getAll() throws SQLException {
        return dao.getAll();
    }

    public EnergyAudit getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(EnergyAudit audit) throws SQLException {
        dao.add(audit);
        LOG.info("Успішно додано вимірювання");
    }

    public void update(EnergyAudit audit) throws SQLException {
        dao.update(audit);
        LOG.info("Успішно оновлено вимірювання");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Успішно видалено вимірювання");    }
}