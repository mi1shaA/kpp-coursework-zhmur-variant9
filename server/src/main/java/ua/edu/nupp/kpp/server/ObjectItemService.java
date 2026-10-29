package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ObjectItemService {
    private static final Logger LOG = Logger.getLogger(MeasureService.class.getName());
    private final ObjectItemDAO dao = new ObjectItemDAO();

    public List<ObjectItem> getAll() throws SQLException {
        return dao.getAll();
    }

    public ObjectItem getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(ObjectItem item) throws SQLException {
        dao.add(item);
        LOG.info("Успішно додано вимірювання");
    }

    public void update(ObjectItem item) throws SQLException {
        dao.update(item);
        LOG.info("Успішно оновлено вимірювання");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Успішно видалено вимірювання");    }
}