package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnergyAuditDAO {

    public void add(EnergyAudit audit) throws SQLException {
        String sql = "INSERT INTO energy_audit (object_id, audit_date, auditor, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, audit.getObjectId());
            ps.setString(2, audit.getAuditDate());
            ps.setString(3, audit.getAuditor());
            ps.setString(4, audit.getNotes());
            ps.executeUpdate();
        }
    }

    public List<EnergyAudit> getAll() throws SQLException {
        String sql = "SELECT id, object_id, audit_date, auditor, status FROM energy_audit ORDER BY id";
        List<EnergyAudit> audits = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                audits.add(new EnergyAudit(
                        rs.getLong("id"),
                        rs.getLong("object_id"),
                        rs.getString("audit_date"),
                        rs.getString("auditor"),
                        rs.getString("status")
                ));
            }
        }
        return audits;
    }

    public EnergyAudit getById(long id) throws SQLException {
        String sql = "SELECT id, object_id, audit_date, auditor, status FROM energy_audit WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new EnergyAudit(
                            rs.getLong("id"),
                            rs.getLong("object_id"),
                            rs.getString("audit_date"),
                            rs.getString("auditor"),
                            rs.getString("status")
                    );
                }
            }
        }
        return null;
    }

    public void update(EnergyAudit audit) throws SQLException {
        String sql = "UPDATE energy_audit SET object_id = ?, audit_date = ?, auditor = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, audit.getObjectId());
            ps.setString(2, audit.getAuditDate());
            ps.setString(3, audit.getAuditor());
            ps.setString(4, audit.getNotes());
            ps.setLong(5, audit.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM energy_audit WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}