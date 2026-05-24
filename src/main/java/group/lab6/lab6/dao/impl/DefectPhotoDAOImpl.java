package group.lab6.lab6.dao.impl;

import group.lab6.lab6.dao.DatabaseConnection;
import group.lab6.lab6.dao.DefectPhotoDAO;
import group.lab6.lab6.model.DefectPhoto;
import group.lab6.lab6.model.UsedDetails;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DefectPhotoDAOImpl implements DefectPhotoDAO {

    private static final String FIND_BY_USED_DETAILS_ID = "SELECT * FROM Defect_Photo WHERE used_details_id = ? AND is_deleted = FALSE ORDER BY created_at";
    private static final String ADD_DEFECT_PHOTO = "{? = call add_defect_photo(?, ?, ?)}";
    private static final String UPDATE_DESCRIPTION = "UPDATE Defect_Photo SET photo_description = ?, updated_at = CURRENT_TIMESTAMP WHERE defect_photo_id = ? AND is_deleted = FALSE";
    private static final String UPDATE_URL = "UPDATE Defect_Photo SET photo_url = ?, updated_at = CURRENT_TIMESTAMP WHERE defect_photo_id = ? AND is_deleted = FALSE";
    private static final String ARCHIVE = "UPDATE Defect_Photo SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE defect_photo_id = ?";

    private DatabaseConnection dbConnection;

    public DefectPhotoDAOImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<DefectPhoto> findByUsedDetailsId(Integer usedDetailsId) {
        List<DefectPhoto> list = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(FIND_BY_USED_DETAILS_ID)) {
            stmt.setInt(1, usedDetailsId);
            ResultSet rs = stmt.executeQuery();
            list = mapperList(rs);
            rs.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при получении фото дефектов: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Integer addDefectPhoto(Integer usedDetailsId, String photoUrl, String description) {
        try (CallableStatement stmt = dbConnection.getConnection().prepareCall(ADD_DEFECT_PHOTO)) {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, usedDetailsId);
            stmt.setString(3, photoUrl);
            stmt.setString(4, description);
            stmt.execute();
            return stmt.getInt(1);
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении фото дефекта: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void updateDescription(Integer defectPhotoId, String description) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(UPDATE_DESCRIPTION)) {
            stmt.setString(1, description);
            stmt.setInt(2, defectPhotoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении описания фото дефекта: " + e.getMessage());
        }
    }

    @Override
    public void updateUrl(Integer defectPhotoId, String photoUrl) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(UPDATE_URL)) {
            stmt.setString(1, photoUrl);
            stmt.setInt(2, defectPhotoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении URL фото дефекта: " + e.getMessage());
        }
    }

    @Override
    public void archive(Integer defectPhotoId) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(ARCHIVE)) {
            stmt.setInt(1, defectPhotoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при архивации фото дефекта: " + e.getMessage());
        }
    }

    private DefectPhoto mapper(ResultSet rs) throws SQLException {
        DefectPhoto photo = new DefectPhoto();
        photo.setDefectPhotoId(rs.getLong("defect_photo_id"));
        photo.setPhotoUrl(rs.getString("photo_url"));
        photo.setPhotoDescription(rs.getString("photo_description"));

        UsedDetails usedDetails = new UsedDetails();
        usedDetails.setUsedDetailsId(rs.getLong("used_details_id"));
        photo.setUsedDetails(usedDetails);
        return photo;
    }

    private List<DefectPhoto> mapperList(ResultSet rs) throws SQLException {
        List<DefectPhoto> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapper(rs));
        }
        return list;
    }
}