package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class MeasureService {

    private static final Logger LOG = Logger.getLogger(MeasureService.class.getName());
    private final MeasureDAO dao = new MeasureDAO();
    private final EnergyAuditDAO auditDAO = new EnergyAuditDAO();

    private void validate(Measure measure) throws SQLException {
        if (measure == null) {
            throw new IllegalArgumentException("Захід не задано");
        }
        if (auditDAO.getById(measure.getAuditId()) == null) {
            throw new IllegalArgumentException("Енергоаудит з ID " + measure.getAuditId() + " не існує");
        }
        ObjectItemService.requireText(measure.getTitle(), "Назва заходу", 200);
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

        double calculatedIndex = MeasureCalculator.calculate(measure.getF1(), measure.getF2(), measure.getF3());
        measure.setIntegralIndex(calculatedIndex);

        // Новий захід завжди отримує початковий стан PROPOSED
        measure.setState(MeasureState.PROPOSED);

        dao.add(measure);
        LOG.info("Успішно додано захід. Розрахований інтегральний індекс: " + calculatedIndex);
    }

    public void update(Measure measure) throws SQLException {
        validate(measure);

        Measure current = dao.getById(measure.getId());
        if (current == null) {
            throw new IllegalArgumentException("Захід з ID " + measure.getId() + " не знайдено");
        }
        // Стан змінюється тільки через changeState(), тут він зберігається без змін
        measure.setState(current.getState());

        double calculatedIndex = MeasureCalculator.calculate(measure.getF1(), measure.getF2(), measure.getF3());
        measure.setIntegralIndex(calculatedIndex);

        dao.update(measure);
        LOG.info("Успішно оновлено захід. Новий інтегральний індекс: " + calculatedIndex);
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Успішно видалено захід із ID: " + id);
    }

    public static boolean isAllowedTransition(MeasureState from, MeasureState to) {
        return (from == MeasureState.PROPOSED && to == MeasureState.APPROVED)
                || (from == MeasureState.APPROVED && to == MeasureState.IMPLEMENTATION)
                || (from == MeasureState.IMPLEMENTATION && to == MeasureState.COMPLETED);
    }

    public void changeState(long id, MeasureState next) throws SQLException {
        if (next == null) {
            throw new IllegalArgumentException("Стан не задано");
        }
        Measure current = dao.getById(id);
        if (current == null) {
            LOG.warning("Помилка: захід не знайдено для зміни стану (ID: " + id + ")");
            throw new IllegalArgumentException("Захід не знайдено");
        }
        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning("Недопустимий перехід стану: " + current.getState() + " -> " + next);
            throw new IllegalArgumentException("Недопустимий перехід стану: " + current.getState() + " → " + next);
        }
        dao.updateState(id, next);
        LOG.info("Змінено стан заходу (ID: " + id + "): " + current.getState() + " -> " + next);
    }
}
