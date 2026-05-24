package group.lab6.lab6.dao.impl;

import group.lab6.lab6.dao.DatabaseConnection;
import group.lab6.lab6.dao.SaleDAO;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleDAOImpl implements SaleDAO {

    private static final String GET_SOLD_REPORT = "{call get_sold_report(?, ?)}";
    private static final String GET_COLLECTION_VALUE = "{? = call get_collection_value()}";

    private DatabaseConnection dbConnection;

    public SaleDAOImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Map<String, Object>> getSoldReport(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> list = new ArrayList<>();
        try (CallableStatement stmt = dbConnection.getConnection().prepareCall(GET_SOLD_REPORT)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("sale_date", rs.getDate("sale_date"));
                row.put("check_number", rs.getString("check_number"));
                row.put("artist", rs.getString("artist"));
                row.put("album_title", rs.getString("album_title"));
                row.put("final_price", rs.getBigDecimal("final_price"));
                row.put("payment_method", rs.getString("payment_method"));
                list.add(row);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при получении отчёта по продажам: " + e.getMessage());
        }
        return list;
    }

    @Override
    public BigDecimal getCollectionValue() {
        try (CallableStatement stmt = dbConnection.getConnection().prepareCall(GET_COLLECTION_VALUE)) {
            stmt.registerOutParameter(1, Types.DECIMAL);
            stmt.execute();
            return stmt.getBigDecimal(1);
        } catch (SQLException e) {
            System.out.println("Ошибка при получении стоимости коллекции: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}