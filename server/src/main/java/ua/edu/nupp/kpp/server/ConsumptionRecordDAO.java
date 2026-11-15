package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsumptionRecordDAO {

    public void add(ConsumptionRecord record) throws SQLException {
        String sql = "INSERT INTO consumption_record (object_id, resource_type, consumption_value, period) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, record.getObjectId());
            ps.setString(2, record.getResourceType());
            ps.setDouble(3, record.getConsumptionValue());
            ps.setString(4, record.getPeriod());
            ps.executeUpdate();
        }
    }

    public List<ConsumptionRecord> getAll() throws SQLException {
        String sql = "SELECT id, object_id, resource_type, consumption_value, period FROM consumption_record ORDER BY id";
        List<ConsumptionRecord> records = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                records.add(new ConsumptionRecord(
                        rs.getLong("id"),
                        rs.getLong("object_id"),
                        rs.getString("resource_type"),
                        rs.getDouble("consumption_value"),
                        rs.getString("period")
                ));
            }
        }
        return records;
    }

    public ConsumptionRecord getById(long id) throws SQLException {
        String sql = "SELECT id, object_id, resource_type, consumption_value, period FROM consumption_record WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ConsumptionRecord(
                            rs.getLong("id"),
                            rs.getLong("object_id"),
                            rs.getString("resource_type"),
                            rs.getDouble("consumption_value"),
                            rs.getString("period")
                    );
                }
            }
        }
        return null;
    }

    public void update(ConsumptionRecord record) throws SQLException {
        String sql = "UPDATE consumption_record SET object_id = ?, resource_type = ?, consumption_value = ?, period = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, record.getObjectId());
            ps.setString(2, record.getResourceType());
            ps.setDouble(3, record.getConsumptionValue());
            ps.setString(4, record.getPeriod());
            ps.setLong(5, record.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM consumption_record WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}