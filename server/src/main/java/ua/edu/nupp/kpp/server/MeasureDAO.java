package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeasureDAO {

    public void add(Measure measure) throws SQLException {
        String sql = "INSERT INTO measure (audit_id, title, state, f1, f2, f3, integral_index) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, measure.getAuditId());
            ps.setString(2, measure.getTitle());
            ps.setString(3, measure.getState());
            ps.setDouble(4, measure.getF1());
            ps.setDouble(5, measure.getF2());
            ps.setDouble(6, measure.getF3());
            ps.setDouble(7, measure.getIntegralIndex());
            ps.executeUpdate();
        }
    }

    public List<Measure> getAll() throws SQLException {
        String sql = "SELECT id, audit_id, title, state, f1, f2, f3, integral_index FROM measure ORDER BY id";
        List<Measure> measures = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                measures.add(new Measure(
                        rs.getLong("id"),
                        rs.getLong("audit_id"),
                        rs.getString("title"),
                        rs.getString("state"),
                        rs.getDouble("f1"),
                        rs.getDouble("f2"),
                        rs.getDouble("f3")
                ));
            }
        }
        return measures;
    }

    public Measure getById(long id) throws SQLException {
        String sql = "SELECT id, audit_id, title, state, f1, f2, f3, integral_index FROM measure WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Measure(
                            rs.getLong("id"),
                            rs.getLong("audit_id"),
                            rs.getString("title"),
                            rs.getString("state"),
                            rs.getDouble("f1"),
                            rs.getDouble("f2"),
                            rs.getDouble("f3")
                    );
                }
            }
        }
        return null;
    }

    public void update(Measure measure) throws SQLException {
        String sql = "UPDATE measure SET audit_id = ?, title = ?, state = ?, f1 = ?, f2 = ?, f3 = ?, integral_index = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, measure.getAuditId());
            ps.setString(2, measure.getTitle());
            ps.setString(3, measure.getState());
            ps.setDouble(4, measure.getF1());
            ps.setDouble(5, measure.getF2());
            ps.setDouble(6, measure.getF3());
            ps.setDouble(7, measure.getIntegralIndex());
            ps.setLong(8, measure.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM measure WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}