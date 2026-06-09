package group.lab6.lab6.dao.impl;

import group.lab6.lab6.dao.DatabaseConnection;
import group.lab6.lab6.dao.InstanceDAO;
import group.lab6.lab6.model.Instance;
import group.lab6.lab6.model.Release;
import group.lab6.lab6.model.FormatType;
import group.lab6.lab6.model.SpeedType;
import group.lab6.lab6.model.InstanceStatus;
import group.lab6.lab6.model.Genre;
import group.lab6.lab6.model.Supplier;
import group.lab6.lab6.model.UsedDetails;
import group.lab6.lab6.model.ConditionGrade;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

public class InstanceDAOImpl implements InstanceDAO {
    private static final Logger logger = LoggerFactory.getLogger(InstanceDAOImpl.class);
    private static final String UPDATE = "UPDATE Instance SET price = ?, location_shelf = ?, location_section = ?, location_box = ?, status = ?::instance_status, format = ?::format_type, speed = ?::speed_type, updated_at = CURRENT_TIMESTAMP WHERE instance_id = ? AND is_deleted = FALSE";
    private static final String ADD_NEW_FROM_SUPPLIER = "{? = call add_new_instance_from_supplier(?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String ADD_USED = "{? = call add_used_instance(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String SELL = "{? = call sell_instance(?, ?, ?, ?)}";
    private static final String ARCHIVE = "{? = call archive_instance(?)}";
    private static final String SEARCH = "{call search_instances(?, ?, ?, ?)}";
    private static final String GET_DETAILS = "SELECT i.*, r.catalog_number, r.artist, r.album_title, r.label, r.country, r.release_year, g.genre_id, g.genre_name, s.supplier_id, s.name as supplier_name, ud.used_details_id, ud.vinyl_condition, ud.cover_condition, ud.defects_notes FROM Instance i JOIN Release r ON i.release_id = r.release_id LEFT JOIN Genre g ON r.genre_id = g.genre_id LEFT JOIN Supplier s ON i.supplier_id = s.supplier_id LEFT JOIN Used_Details ud ON i.used_details_id = ud.used_details_id WHERE i.instance_id = ? AND i.is_deleted = FALSE";
    private static final String GET_ALL_GENRES = "SELECT genre_id, genre_name FROM Genre WHERE is_deleted = FALSE ORDER BY genre_name";
    private static final String GET_ALL_SUPPLIERS = "SELECT supplier_id, name, contact_info FROM Supplier WHERE is_deleted = FALSE ORDER BY name";
    private DatabaseConnection dbConnection;

    public InstanceDAOImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Instance update(Instance instance) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(UPDATE)) {
            stmt.setBigDecimal(1, instance.getPrice());
            stmt.setString(2, instance.getLocationShelf());
            stmt.setString(3, instance.getLocationSection());
            stmt.setString(4, instance.getLocationBox());
            stmt.setString(5, instance.getStatus().toDbValue());
            stmt.setString(6, instance.getFormat().name());
            stmt.setString(7, instance.getSpeed().toDbValue());
            stmt.setLong(8, instance.getInstanceId());
            stmt.executeUpdate();
            return instance;
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении экземпляра: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Integer addNewFromSupplier(String catalogNumber, BigDecimal price, String format,
                                      String speed, Integer supplierId, String locationShelf,
                                      String locationSection, String locationBox) {
        logger.debug("Вызов add_new_instance_from_supplier для каталога {}", catalogNumber);
        try (CallableStatement stmt = dbConnection.getConnection().prepareCall(ADD_NEW_FROM_SUPPLIER)) {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, catalogNumber);
            stmt.setBigDecimal(3, price);
            stmt.setString(4, format);
            stmt.setString(5, speed);
            stmt.setInt(6, supplierId);
            stmt.setString(7, locationShelf);
            stmt.setString(8, locationSection);
            stmt.setString(9, locationBox);
            stmt.execute();
            return stmt.getInt(1);
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении нового экземпляра: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Integer addUsed(String catalogNumber, BigDecimal price, String format, String speed,
                           String vinylCondition, String coverCondition, String defectsNotes,
                           String sellerFirstName, String sellerLastName, String sellerPhone,
                           String locationShelf, String locationSection, String locationBox) {
        try (CallableStatement stmt = dbConnection.getConnection().prepareCall(ADD_USED)) {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, catalogNumber);
            stmt.setBigDecimal(3, price);
            stmt.setString(4, format);
            stmt.setString(5, speed);
            stmt.setString(6, vinylCondition);
            stmt.setString(7, coverCondition);
            stmt.setString(8, defectsNotes);
            stmt.setString(9, sellerFirstName);
            stmt.setString(10, sellerLastName);
            stmt.setString(11, sellerPhone);
            stmt.setString(12, locationShelf);
            stmt.setString(13, locationSection);
            stmt.setString(14, locationBox);
            stmt.execute();
            return stmt.getInt(1);
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении Б/У экземпляра: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Integer sell(Integer instanceId, String checkNumber, String paymentMethod, BigDecimal finalPrice) {
        try (CallableStatement stmt = dbConnection.getConnection().prepareCall(SELL)) {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, instanceId);
            stmt.setString(3, checkNumber);
            stmt.setString(4, paymentMethod);
            if (finalPrice != null) {
                stmt.setBigDecimal(5, finalPrice);
            } else {
                stmt.setNull(5, Types.DECIMAL);
            }
            stmt.execute();
            return stmt.getInt(1);
        } catch (SQLException e) {
            System.out.println("Ошибка при продаже экземпляра: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean archive(Integer instanceId) {
        try (CallableStatement stmt = dbConnection.getConnection().prepareCall(ARCHIVE)) {
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, instanceId);
            stmt.execute();
            return stmt.getBoolean(1);
        } catch (SQLException e) {
            System.out.println("Ошибка при архивации экземпляра: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Instance> search(String artist, String albumTitle, String genreName, String vinylCondition) {
        String sql = "{call search_instances(?, ?, ?, ?)}";
        List<Instance> instances = new ArrayList<>();
        try (CallableStatement stmt = dbConnection.getConnection().prepareCall(sql)) {
            stmt.setString(1, artist);
            stmt.setString(2, albumTitle);
            stmt.setString(3, genreName);
            stmt.setString(4, vinylCondition);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Instance instance = new Instance();
                instance.setInstanceId(rs.getLong("instance_id"));
                instance.setPrice(rs.getBigDecimal("price"));
                instance.setLocationShelf(rs.getString("location_shelf"));
                instance.setLocationSection(rs.getString("location_section"));
                instance.setLocationBox(rs.getString("location_box"));
                instance.setStatus(InstanceStatus.fromDbValue(rs.getString("status")));

                if (rs.getObject("format") != null) {
                    instance.setFormat(FormatType.valueOf(rs.getString("format")));
                }
                if (rs.getObject("speed") != null) {
                    instance.setSpeed(SpeedType.fromDbValue(rs.getString("speed")));
                }

                Release release = new Release();
                release.setArtist(rs.getString("artist"));
                release.setAlbumTitle(rs.getString("album_title"));
                if (rs.getObject("genre_name") != null) {
                    Genre genre = new Genre();
                    genre.setGenreName(rs.getString("genre_name"));
                    release.setGenre(genre);
                }
                instance.setRelease(release);

//                if (rs.getObject("vinyl_condition") != null) {
//                    UsedDetails ud = new UsedDetails();
//                    ud.setVinylCondition(ConditionGrade.fromDbValue(rs.getString("vinyl_condition")));
//                    ud.setCoverCondition(ConditionGrade.fromDbValue(rs.getString("cover_condition")));
//                    instance.setUsedDetails(ud);
//                }

                if (rs.getObject("vinyl_condition") != null || rs.getObject("cover_condition") != null) {
                    UsedDetails ud = new UsedDetails();
                    if (rs.getObject("vinyl_condition") != null) {
                        ud.setVinylCondition(ConditionGrade.fromDbValue(rs.getString("vinyl_condition")));
                    }
                    if (rs.getObject("cover_condition") != null) {
                        ud.setCoverCondition(ConditionGrade.fromDbValue(rs.getString("cover_condition")));
                    }
                    ud.setDefectsNotes(rs.getString("defects_notes"));
                    if (rs.getObject("used_details_id") != null) {
                        ud.setUsedDetailsId(rs.getLong("used_details_id"));
                    }
                    instance.setUsedDetails(ud);
                }

                instances.add(instance);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при поиске экземпляров: " + e.getMessage());
        }
        return instances;
    }

    @Override
    public Optional<Instance> getDetails(Integer instanceId) {
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(GET_DETAILS)) {
            stmt.setInt(1, instanceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapperFull(rs));
            }
            rs.close();
            return Optional.empty();
        } catch (SQLException e) {
            System.out.println("Ошибка при получении деталей экземпляра: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        List<Genre> list = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(GET_ALL_GENRES);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Genre genre = new Genre();
                genre.setGenreId(rs.getLong("genre_id"));
                genre.setGenreName(rs.getString("genre_name"));
                list.add(genre);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении списка жанров: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(GET_ALL_SUPPLIERS);
             ResultSet rs = stmt.executeQuery()) {
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

    private Instance mapper(ResultSet rs) throws SQLException {
        Instance instance = new Instance();
        instance.setInstanceId(rs.getLong("instance_id"));
        instance.setPrice(rs.getBigDecimal("price"));
        instance.setLocationShelf(rs.getString("location_shelf"));
        instance.setLocationSection(rs.getString("location_section"));
        instance.setLocationBox(rs.getString("location_box"));
        instance.setStatus(InstanceStatus.fromDbValue(rs.getString("status")));
        if (rs.getObject("format") != null) {
            instance.setFormat(FormatType.valueOf(rs.getString("format")));
        }
        if (rs.getObject("speed") != null) {
            instance.setSpeed(SpeedType.fromDbValue(rs.getString("speed")));
        }

        Release release = new Release();
        release.setCatalogNumber(rs.getString("catalog_number"));
        release.setArtist(rs.getString("artist"));
        release.setAlbumTitle(rs.getString("album_title"));
        instance.setRelease(release);

        if (rs.getObject("vinyl_condition") != null) {
            UsedDetails ud = new UsedDetails();
            ud.setVinylCondition(ConditionGrade.fromDbValue(rs.getString("vinyl_condition")));
            ud.setCoverCondition(ConditionGrade.fromDbValue(rs.getString("cover_condition")));
            instance.setUsedDetails(ud);
        }
        return instance;
    }

    private Instance mapperFull(ResultSet rs) throws SQLException {
        Instance instance = mapper(rs);
        Release release = instance.getRelease();
        release.setReleaseId(rs.getLong("release_id"));
        release.setLabel(rs.getString("label"));
        release.setCountry(rs.getString("country"));
        int releaseYear = rs.getInt("release_year");
        if (!rs.wasNull()) {
            release.setReleaseYear(releaseYear);
        }

        long genreId = rs.getLong("genre_id");
        if (!rs.wasNull()) {
            Genre genre = new Genre();
            genre.setGenreId(genreId);
            genre.setGenreName(rs.getString("genre_name"));
            release.setGenre(genre);
        }

        long supplierId = rs.getLong("supplier_id");
        if (!rs.wasNull()) {
            Supplier supplier = new Supplier();
            supplier.setSupplierId(supplierId);
            supplier.setName(rs.getString("supplier_name"));
            instance.setSupplier(supplier);
        }

        long usedDetailsId = rs.getLong("used_details_id");
        if (!rs.wasNull()) {
            UsedDetails ud = instance.getUsedDetails();
            if (ud == null) {
                ud = new UsedDetails();
            }
            ud.setUsedDetailsId(usedDetailsId);
            ud.setDefectsNotes(rs.getString("defects_notes"));
            instance.setUsedDetails(ud);
        }
        return instance;
    }

    private List<Instance> mapperList(ResultSet rs) throws SQLException {
        List<Instance> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapper(rs));
        }
        return list;
    }
}