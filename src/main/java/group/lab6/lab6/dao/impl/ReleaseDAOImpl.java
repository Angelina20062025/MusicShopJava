package group.lab6.lab6.dao.impl;

import group.lab6.lab6.dao.DatabaseConnection;
import group.lab6.lab6.dao.ReleaseDAO;
import group.lab6.lab6.model.Release;
import group.lab6.lab6.model.Genre;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReleaseDAOImpl implements ReleaseDAO {

    private static final String UPDATE = "UPDATE Release SET artist = ?, album_title = ?, genre_id = ?, label = ?, country = ?, release_year = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE release_id = ? AND is_deleted = FALSE";
    private static final String ADD_OR_GET = "{? = call add_or_get_release(?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String FIND_BY_CATALOG_NUMBER = "SELECT r.*, g.genre_id, g.genre_name FROM Release r LEFT JOIN Genre g ON r.genre_id = g.genre_id WHERE r.catalog_number = ? AND r.is_deleted = FALSE";
    private static final String SEARCH = "SELECT r.*, g.genre_id, g.genre_name FROM Release r LEFT JOIN Genre g ON r.genre_id = g.genre_id WHERE r.is_deleted = FALSE AND (? IS NULL OR r.artist ILIKE '%' || ? || '%') AND (? IS NULL OR r.album_title ILIKE '%' || ? || '%') AND (? IS NULL OR g.genre_name ILIKE '%' || ? || '%') ORDER BY r.artist, r.album_title";
    private static final String ARCHIVE = "UPDATE Release SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE release_id = ?";
    private DatabaseConnection dbConnection;

    public ReleaseDAOImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Release update(Release release) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(UPDATE)) {
            stmt.setString(1, release.getArtist());
            stmt.setString(2, release.getAlbumTitle());
            if (release.getGenre() != null && release.getGenre().getGenreId() != null) {
                stmt.setLong(3, release.getGenre().getGenreId());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }
            stmt.setString(4, release.getLabel());
            stmt.setString(5, release.getCountry());
            if (release.getReleaseYear() != null) {
                stmt.setInt(6, release.getReleaseYear());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setString(7, release.getDescription());
            stmt.setLong(8, release.getReleaseId());
            stmt.executeUpdate();
            return release;
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении релиза: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Integer addOrGet(String catalogNumber, String artist, String albumTitle, Integer genreId) {
        try (CallableStatement stmt = dbConnection.getConnection().prepareCall(ADD_OR_GET)) {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, catalogNumber);
            stmt.setString(3, artist);
            stmt.setString(4, albumTitle);
            if (genreId != null) {
                stmt.setInt(5, genreId);
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setNull(6, Types.VARCHAR);
            stmt.setNull(7, Types.VARCHAR);
            stmt.setNull(8, Types.INTEGER);
            stmt.setNull(9, Types.VARCHAR);
            stmt.execute();
            return stmt.getInt(1);
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении/получении релиза: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<Release> findByCatalogNumber(String catalogNumber) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(FIND_BY_CATALOG_NUMBER)) {
            stmt.setString(1, catalogNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapper(rs));
            }
            rs.close();
            return Optional.empty();
        } catch (SQLException e) {
            System.out.println("Ошибка при поиске релиза по каталоговому номеру: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Release> search(String artist, String albumTitle, String genreName) {
        List<Release> list = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(SEARCH)) {
            stmt.setString(1, artist);
            stmt.setString(2, artist);
            stmt.setString(3, albumTitle);
            stmt.setString(4, albumTitle);
            stmt.setString(5, genreName);
            stmt.setString(6, genreName);
            ResultSet rs = stmt.executeQuery();
            list = mapperList(rs);
            rs.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при поиске релизов: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void archive(Integer releaseId) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(ARCHIVE)) {
            stmt.setInt(1, releaseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при архивации релиза: " + e.getMessage());
        }
    }

    private Release mapper(ResultSet rs) throws SQLException {
        Release release = new Release();
        release.setReleaseId(rs.getLong("release_id"));
        release.setCatalogNumber(rs.getString("catalog_number"));
        release.setArtist(rs.getString("artist"));
        release.setAlbumTitle(rs.getString("album_title"));
        release.setLabel(rs.getString("label"));
        release.setCountry(rs.getString("country"));
        int releaseYear = rs.getInt("release_year");
        if (!rs.wasNull()) {
            release.setReleaseYear(releaseYear);
        }
        release.setDescription(rs.getString("description"));
        release.setNumberOfCopies(rs.getInt("number_of_copies"));
        long genreId = rs.getLong("genre_id");
        if (!rs.wasNull()) {
            Genre genre = new Genre();
            genre.setGenreId(genreId);
            genre.setGenreName(rs.getString("genre_name"));
            release.setGenre(genre);
        }
        return release;
    }

    private List<Release> mapperList(ResultSet rs) throws SQLException {
        List<Release> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapper(rs));
        }
        return list;
    }
}