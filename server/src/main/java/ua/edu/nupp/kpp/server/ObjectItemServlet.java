package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectItemServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ObjectItemServlet.class.getName());
    private final ObjectItemService service = new ObjectItemService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            writeJson(resp, HttpServletResponse.SC_OK, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        try {
            if ("add".equals(action)) {
                service.add(read(req, false));
            } else if ("update".equals(action)) {
                service.update(read(req, true));
            } else if ("delete".equals(action)) {
                service.delete(parseLong(req, "id"));
            } else {
                throw new IllegalArgumentException("Невідома дія: " + action);
            }
            writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "ok"));
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private ObjectItem read(HttpServletRequest req, boolean withId) {
        ObjectItem item = new ObjectItem();
        if (withId) {
            item.setId(parseLong(req, "id"));
        }
        item.setCode(text(req, "code"));
        item.setName(text(req, "name"));
        item.setAddress(text(req, "address"));
        return item;
    }

    private String text(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? null : value.trim();
    }

    private long parseLong(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        try {
            return Long.parseLong(value == null ? "" : value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр " + name + " повинен бути цілим числом");
        }
    }

    private double parseDouble(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        try {
            return Double.parseDouble(value == null ? "" : value.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр " + name + " повинен бути числом");
        }
    }

    private void writeJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(resp.getWriter(), body);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        if (e instanceof IllegalArgumentException) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", String.valueOf(e.getMessage())));
        } else {
            LOG.log(Level.SEVERE, "Внутрішня помилка сервера", e);
            writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Внутрішня помилка сервера"));
        }
    }
}
