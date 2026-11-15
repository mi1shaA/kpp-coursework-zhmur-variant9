package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ObjectItemService {
    private static final Logger LOG = Logger.getLogger(ObjectItemService.class.getName());
    private final ObjectItemDAO dao = new ObjectItemDAO();

    private void validate(ObjectItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Об'єкт не задано");
        }
        requireText(item.getCode(), "Код об'єкта", 40);
        requireText(item.getName(), "Назва об'єкта", 160);
        requireText(item.getAddress(), "Адреса об'єкта", 200);
    }

    static void requireText(String value, String field, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " не може бути порожнім");
        }
        if (value.trim().length() > maxLength) {
            throw new IllegalArgumentException(field + " не може бути довшим за " + maxLength + " символів");
        }
    }

    public List<ObjectItem> getAll() throws SQLException {
        return dao.getAll();
    }

    public ObjectItem getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(ObjectItem item) throws SQLException {
        validate(item);
        dao.add(item);
        LOG.info("Успішно додано об'єкт: " + item.getCode());
    }

    public void update(ObjectItem item) throws SQLException {
        validate(item);
        if (dao.getById(item.getId()) == null) {
            throw new IllegalArgumentException("Об'єкт з ID " + item.getId() + " не знайдено");
        }
        dao.update(item);
        LOG.info("Успішно оновлено об'єкт (ID: " + item.getId() + ")");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Успішно видалено об'єкт (ID: " + id + ")");
    }
}
