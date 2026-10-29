package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class MeasureService {

    private static final Logger LOG = Logger.getLogger(MeasureService.class.getName());
    private final MeasureDAO dao = new MeasureDAO();
    private final EnergyAuditDAO auditDAO = new EnergyAuditDAO();

    private void validate(Measure measure) {
        if (measure.getF1() < 0 || measure.getF1() > 100 ||
                measure.getF2() < 0 || measure.getF2() > 100 ||
                measure.getF3() < 0 || measure.getF3() > 100) {
            throw new IllegalArgumentException("Помилка: фактори f1, f2 та f3 повинні знаходитися в діапазоні від 0 до 100!");
        }
    }

    public List<Measure> getAll() throws SQLException {
        return dao.getAll();
    }

    public Measure getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Measure measure) throws SQLException {
        validate(measure);
        dao.add(measure);
        LOG.info("Успішно додано вимірювання");
    }

    public void update(Measure measure) throws SQLException {
        validate(measure);
        dao.update(measure);
        LOG.info("Успішно оновлено вимірювання");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Успішно видалено вимірювання");    }
}