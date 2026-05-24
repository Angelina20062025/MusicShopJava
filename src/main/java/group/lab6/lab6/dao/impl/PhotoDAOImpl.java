package group.lab6.lab6.dao.impl;

import group.lab6.lab6.dao.DatabaseConnection;
import group.lab6.lab6.dao.PhotoDAO;
import group.lab6.lab6.model.Photo;
import group.lab6.lab6.model.Instance;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PhotoDAOImpl implements PhotoDAO {

    private static final String FIND_BY_INSTANCE_ID = "SELECT * FROM Photo WHERE instance_id = ? AND is_deleted = FALSE ORDER BY created_at";
    private static final String ADD_PHOTO = "{? = call add_photo_to_instance(?, ?, ?)}";
    private static final String UPDATE_DESCRIPTION = "UPDATE Photo SET photo_description = ?, updated_at = CURRENT_TIMESTAMP WHERE photo_id = ? AND is_deleted = FALSE";
    private static final String UPDATE_URL = "UPDATE Photo SET photo_url = ?, updated_at = CURRENT_TIMESTAMP WHERE photo_id = ? AND is_deleted = FALSE";
    private static final String ARCHIVE = "UPDATE Photo SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE photo_id = ?";

    private DatabaseConnection dbConnection;

    public PhotoDAOImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Photo> findByInstanceId(Integer instanceId) {
        List<Photo> list = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(FIND_BY_INSTANCE_ID)) {
            stmt.setInt(1, instanceId);
            ResultSet rs = stmt.executeQuery();
            list = mapperList(rs);
            rs.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при получении фото: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Integer addPhoto(Integer instanceId, String photoUrl, String description) {
        try (CallableStatement stmt = dbConnection.getConnection().prepareCall(ADD_PHOTO)) {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, instanceId);
            stmt.setString(3, photoUrl);
            stmt.setString(4, description);
            stmt.execute();
            return stmt.getInt(1);
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении фото: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void updateDescription(Integer photoId, String description) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(UPDATE_DESCRIPTION)) {
            stmt.setString(1, description);
            stmt.setInt(2, photoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении описания фото: " + e.getMessage());
        }
    }

    @Override
    public void updateUrl(Integer photoId, String photoUrl) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(UPDATE_URL)) {
            stmt.setString(1, photoUrl);
            stmt.setInt(2, photoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении URL фото: " + e.getMessage());
        }
    }

    @Override
    public void archive(Integer photoId) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(ARCHIVE)) {
            stmt.setInt(1, photoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при архивации фото: " + e.getMessage());
        }
    }

    private Photo mapper(ResultSet rs) throws SQLException {
        Photo photo = new Photo();
        photo.setPhotoId(rs.getLong("photo_id"));
        photo.setPhotoUrl(rs.getString("photo_url"));
        photo.setPhotoDescription(rs.getString("photo_description"));

        Instance instance = new Instance();
        instance.setInstanceId(rs.getLong("instance_id"));
        photo.setInstance(instance);
        return photo;
    }

    private List<Photo> mapperList(ResultSet rs) throws SQLException {
        List<Photo> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapper(rs));
        }
        return list;
    }
}