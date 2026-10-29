package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjectItemDAO {

    public void add(ObjectItem item) throws SQLException {
        String sql = "INSERT INTO object_item (code, name, address) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getCode());
            ps.setString(2, item.getName());
            ps.setString(3, item.getAddress());
            ps.executeUpdate();
        }
    }

    public List<ObjectItem> getAll() throws SQLException {
        String sql = "SELECT id, code, name, address FROM object_item ORDER BY id";
        List<ObjectItem> items = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(new ObjectItem(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("address")
                ));
            }
        }
        return items;
    }

    public ObjectItem getById(long id) throws SQLException {
        String sql = "SELECT id, code, name, address FROM object_item WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ObjectItem(
                            rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("address")
                    );
                }
            }
        }
        return null;
    }

    public void update(ObjectItem item) throws SQLException {
        String sql = "UPDATE object_item SET code = ?, name = ?, address = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getCode());
            ps.setString(2, item.getName());
            ps.setString(3, item.getAddress());
            ps.setLong(4, item.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM object_item WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}