package group.lab6.lab6.service;

import group.lab6.lab6.model.Instance;
import java.math.BigDecimal;
import java.util.List;

public interface VinylService {

    Integer addNewInstance(String catalogNumber, String artist, String albumTitle,
                           Integer genreId, BigDecimal price, String format, String speed,
                           Integer supplierId, String locationShelf, String locationSection,
                           String locationBox);

    Integer addUsedInstance(String catalogNumber, String artist, String albumTitle,
                            Integer genreId, BigDecimal price, String format, String speed,
                            String vinylCondition, String coverCondition, String defectsNotes,
                            String sellerFirstName, String sellerLastName, String sellerPhone,
                            String locationShelf, String locationSection, String locationBox);

    Integer sellInstance(Integer instanceId, String checkNumber, String paymentMethod, BigDecimal finalPrice);

    List<Instance> searchInstances(String artist, String albumTitle, String genreName, String vinylCondition);

    void updateInstance(Instance instance);

    void archiveInstance(Integer instanceId);
}