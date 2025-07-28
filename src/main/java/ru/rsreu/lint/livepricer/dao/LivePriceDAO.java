package ru.rsreu.lint.livepricer.dao;

import ru.rsreu.lint.livepricer.ResourceLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LivePriceDAO {

    public void updateItemPrice(String itemId, String manufacturer, double price) throws SQLException {
        String sql = ResourceLoader.getProperty("sql-query.update_item_price");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemId);
            ps.setString(2, manufacturer);
            ps.setDouble(3, price);
            ps.executeUpdate();
        }
    }

    public double getAveragePrice(String itemId) throws SQLException {
        String sql = ResourceLoader.getProperty("sql-query.get_average_price");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double avg = rs.getDouble(1);
                boolean isNull = rs.wasNull();
                return isNull ? 0.0 : avg;
            }
            return 0.0;
        }
    }
}
