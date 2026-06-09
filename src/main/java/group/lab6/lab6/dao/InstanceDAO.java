package group.lab6.lab6.dao;
import group.lab6.lab6.model.Genre;
import group.lab6.lab6.model.Instance;
import group.lab6.lab6.model.Supplier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Предоставляет методы для работы с экземплярами виниловых пластинок в базе данных.
 */
public interface InstanceDAO {
    Instance update(Instance instance);

    Integer addNewFromSupplier(String catalogNumber, BigDecimal price, String format,
                               String speed, Integer supplierId, String locationShelf,
                               String locationSection, String locationBox);
    Integer addUsed(String catalogNumber, BigDecimal price, String format, String speed,
                    String vinylCondition, String coverCondition, String defectsNotes,
                    String sellerFirstName, String sellerLastName, String sellerPhone,
                    String locationShelf, String locationSection, String locationBox);
    Integer sell(Integer instanceId, String checkNumber, String paymentMethod, BigDecimal finalPrice);
    boolean archive(Integer instanceId);
    List<Instance> search(String artist, String albumTitle, String genreName, String vinylCondition);
    Optional<Instance> getDetails(Integer instanceId);
    List<Genre> getAllGenres();
    List<Supplier> getAllSuppliers();
}