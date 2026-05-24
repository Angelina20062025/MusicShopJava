package group.lab6.lab6.dao.impl;

import group.lab6.lab6.dao.DatabaseConnection;
import group.lab6.lab6.dao.SupplierDAO;
import group.lab6.lab6.model.Supplier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAOImpl implements SupplierDAO {

    private static final String FIND_ALL = "SELECT * FROM Supplier WHERE is_deleted = FALSE ORDER BY name";
    private static final String SAVE = "INSERT INTO Supplier (name, contact_info) VALUES (?, ?) RETURNING supplier_id";
    private static final String UPDATE = "UPDATE Supplier SET name = ?, contact_info = ?, updated_at = CURRENT_TIMESTAMP WHERE supplier_id = ? AND is_deleted = FALSE";
    private static final String ARCHIVE = "UPDATE Supplier SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE supplier_id = ?";

    private DatabaseConnection dbConnection;

    public SupplierDAOImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Supplier> findAll() {
        List<Supplier> list = new ArrayList<>();
        try (Statement stmt = dbConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(FIND_ALL)) {
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierId(rs.getLong("supplier_id"));
                supplier.setName(rs.getString("name"));
                supplier.setContactInfo(rs.getString("contact_info"));
                list.add(supplier);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении списка поставщиков: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Supplier save(Supplier supplier) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(SAVE)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactInfo());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                supplier.setSupplierId(rs.getLong("supplier_id"));
            }
            rs.close();
            return supplier;
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении поставщика: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Supplier update(Supplier supplier) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(UPDATE)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactInfo());
            stmt.setLong(3, supplier.getSupplierId());
            stmt.executeUpdate();
            return supplier;
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении поставщика: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void archive(Integer supplierId) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(ARCHIVE)) {
            stmt.setInt(1, supplierId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при архивации поставщика: " + e.getMessage());
        }
    }
}