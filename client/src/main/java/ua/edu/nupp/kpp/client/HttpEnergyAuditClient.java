package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class HttpEnergyAuditClient {
    private static final String BASE = "http://localhost:8080/energy-audit-server";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public List<ObjectItem> getObjects() {
        return Arrays.asList(read(get("/objects"), ObjectItem[].class));
    }

    public void addObject(ObjectItem o) {
        post("/objects", form("add", null, "code", o.getCode(), "name", o.getName(), "address", o.getAddress()));
    }

    public void updateObject(ObjectItem o) {
        post("/objects", form("update", o.getId(), "code", o.getCode(), "name", o.getName(), "address", o.getAddress()));
    }

    public void deleteObject(long id) {
        post("/objects", form("delete", id));
    }

    public List<EnergyAudit> getAudits() {
        return Arrays.asList(read(get("/audits"), EnergyAudit[].class));
    }

    public void addAudit(EnergyAudit a) {
        post("/audits", form("add", null, "objectId", a.getObjectId(), "auditDate", a.getAuditDate(),
                "auditor", a.getAuditor(), "notes", a.getNotes()));
    }

    public void updateAudit(EnergyAudit a) {
        post("/audits", form("update", a.getId(), "objectId", a.getObjectId(), "auditDate", a.getAuditDate(),
                "auditor", a.getAuditor(), "notes", a.getNotes()));
    }

    public void deleteAudit(long id) {
        post("/audits", form("delete", id));
    }

    public List<Measure> getMeasures() {
        return Arrays.asList(read(get("/measures"), Measure[].class));
    }

    public void addMeasure(Measure m) {
        post("/measures", form("add", null, "auditId", m.getAuditId(), "title", m.getTitle(),
                "f1", m.getF1(), "f2", m.getF2(), "f3", m.getF3()));
    }

    public void updateMeasure(Measure m) {
        post("/measures", form("update", m.getId(), "auditId", m.getAuditId(), "title", m.getTitle(),
                "f1", m.getF1(), "f2", m.getF2(), "f3", m.getF3()));
    }

    public void changeMeasureState(long id, MeasureState state) {
        post("/measures", form("transition", id, "state", state.name()));
    }

    public void deleteMeasure(long id) {
        post("/measures", form("delete", id));
    }

    public List<ConsumptionRecord> getRecords() {
        return Arrays.asList(read(get("/records"), ConsumptionRecord[].class));
    }

    public void addRecord(ConsumptionRecord r) {
        post("/records", form("add", null, "objectId", r.getObjectId(), "resourceType", r.getResourceType(),
                "consumptionValue", r.getConsumptionValue(), "period", r.getPeriod()));
    }

    public void updateRecord(ConsumptionRecord r) {
        post("/records", form("update", r.getId(), "objectId", r.getObjectId(), "resourceType", r.getResourceType(),
                "consumptionValue", r.getConsumptionValue(), "period", r.getPeriod()));
    }

    public void deleteRecord(long id) {
        post("/records", form("delete", id));
    }

    private String form(String action, Long id, Object... pairs) {
        StringBuilder sb = new StringBuilder("action=").append(enc(action));
        if (id != null) {
            sb.append("&id=").append(id);
        }
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            sb.append('&').append(pairs[i]).append('=').append(enc(pairs[i + 1] == null ? "" : String.valueOf(pairs[i + 1])));
        }
        return sb.toString();
    }

    private String form(String action, long id) {
        return form(action, Long.valueOf(id), new Object[0]);
    }

    private String get(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        return send(req);
    }

    private String post(String path, String body) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        return send(req);
    }

    private String send(HttpRequest req) {
        HttpResponse<String> resp;
        try {
            resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (ConnectException e) {
            throw new RuntimeException("Сервер недоступний. Перевірте, що Tomcat запущений.", e);
        } catch (IOException e) {
            throw new RuntimeException("Помилка з'єднання із сервером: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Запит було перервано", e);
        }
        if (resp.statusCode() >= 400) {
            throw new RuntimeException(extractError(resp.body(), resp.statusCode()));
        }
        return resp.body();
    }

    private String extractError(String body, int status) {
        try {
            JsonNode node = mapper.readTree(body);
            if (node != null && node.hasNonNull("error")) {
                return node.get("error").asText();
            }
        } catch (IOException ignored) {
            // тіло не є JSON — нижче повертаємо загальне повідомлення
        }
        return "Помилка сервера (HTTP " + status + ")";
    }

    private <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException("Некоректна JSON-відповідь сервера", e);
        }
    }

    private String enc(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
