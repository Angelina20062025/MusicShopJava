package group.lab6.lab6.dao.impl;

import group.lab6.lab6.dao.DatabaseConnection;
import group.lab6.lab6.dao.SellerPersonDAO;
import group.lab6.lab6.model.SellerPerson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SellerPersonDAOImpl implements SellerPersonDAO {

    private static final String FIND_ALL = "SELECT * FROM Seller_Person WHERE is_deleted = FALSE ORDER BY last_name, first_name";
    private static final String SAVE = "INSERT INTO Seller_Person (first_name, last_name, patronymic, phone) VALUES (?, ?, ?, ?) RETURNING person_id";
    private static final String UPDATE = "UPDATE Seller_Person SET first_name = ?, last_name = ?, patronymic = ?, phone = ?, updated_at = CURRENT_TIMESTAMP WHERE person_id = ? AND is_deleted = FALSE";
    private static final String ARCHIVE = "UPDATE Seller_Person SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE person_id = ?";

    private final DatabaseConnection dbConnection;

    public SellerPersonDAOImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<SellerPerson> findAll() {
        List<SellerPerson> list = new ArrayList<>();
        try (Statement stmt = dbConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(FIND_ALL)) {
            while (rs.next()) {
                list.add(mapResultSetToSeller(rs));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении списка продавцов: " + e.getMessage());
        }
        return list;
    }

    @Override
    public SellerPerson save(SellerPerson seller) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(SAVE)) {
            stmt.setString(1, seller.getFirstName());
            stmt.setString(2, seller.getLastName());
            stmt.setString(3, seller.getPatronymic());
            stmt.setString(4, seller.getPhone());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                seller.setPersonId(rs.getLong("person_id"));
            }
            rs.close();
            return seller;
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении продавца: " + e.getMessage());
            return null;
        }
    }

    @Override
    public SellerPerson update(SellerPerson seller) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(UPDATE)) {
            stmt.setString(1, seller.getFirstName());
            stmt.setString(2, seller.getLastName());
            stmt.setString(3, seller.getPatronymic());
            stmt.setString(4, seller.getPhone());
            stmt.setLong(5, seller.getPersonId());
            stmt.executeUpdate();
            return seller;
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении продавца: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void archive(Integer sellerId) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(ARCHIVE)) {
            stmt.setInt(1, sellerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при архивации продавца: " + e.getMessage());
        }
    }

    private SellerPerson mapResultSetToSeller(ResultSet rs) throws SQLException {
        SellerPerson seller = new SellerPerson();
        seller.setPersonId(rs.getLong("person_id"));
        seller.setFirstName(rs.getString("first_name"));
        seller.setLastName(rs.getString("last_name"));
        seller.setPatronymic(rs.getString("patronymic"));
        seller.setPhone(rs.getString("phone"));
        return seller;
    }
}