package group.lab6.lab6.service.impl;

import group.lab6.lab6.dao.InstanceDAO;
import group.lab6.lab6.dao.ReleaseDAO;
import group.lab6.lab6.dao.PhotoDAO;
import group.lab6.lab6.model.Instance;
import group.lab6.lab6.service.VinylService;
import group.lab6.lab6.service.exceptions.ValidationException;
import group.lab6.lab6.service.exceptions.InstanceNotAvailableException;

import java.math.BigDecimal;
import java.util.List;

public class VinylServiceImpl implements VinylService {

    private final InstanceDAO instanceDAO;
    private final ReleaseDAO releaseDAO;
    private final PhotoDAO photoDAO;

    public VinylServiceImpl(InstanceDAO instanceDAO, ReleaseDAO releaseDAO, PhotoDAO photoDAO) {
        this.instanceDAO = instanceDAO;
        this.releaseDAO = releaseDAO;
        this.photoDAO = photoDAO;
    }

    @Override
    public Integer addNewInstance(String catalogNumber, String artist, String albumTitle,
                                  Integer genreId, BigDecimal price, String format, String speed,
                                  Integer supplierId, String locationShelf, String locationSection,
                                  String locationBox) {

        if (catalogNumber == null || catalogNumber.trim().isEmpty()) {
            throw new ValidationException("Каталоговый номер не может быть пустым");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Цена должна быть больше 0");
        }
        if (supplierId == null) {
            throw new ValidationException("Необходимо выбрать поставщика");
        }

        Integer releaseId = releaseDAO.addOrGet(catalogNumber, artist, albumTitle, genreId);

        return instanceDAO.addNewFromSupplier(catalogNumber, price, format, speed,
                supplierId, locationShelf, locationSection, locationBox);
    }

    @Override
    public Integer addUsedInstance(String catalogNumber, String artist, String albumTitle,
                                   Integer genreId, BigDecimal price, String format, String speed,
                                   String vinylCondition, String coverCondition, String defectsNotes,
                                   String sellerFirstName, String sellerLastName, String sellerPhone,
                                   String locationShelf, String locationSection, String locationBox) {

        if (catalogNumber == null || catalogNumber.trim().isEmpty()) {
            throw new ValidationException("Каталоговый номер не может быть пустым");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Цена должна быть больше 0");
        }
        if (sellerFirstName == null || sellerFirstName.trim().isEmpty()) {
            throw new ValidationException("Имя продавца не может быть пустым");
        }
        if (sellerLastName == null || sellerLastName.trim().isEmpty()) {
            throw new ValidationException("Фамилия продавца не может быть пустой");
        }

        releaseDAO.addOrGet(catalogNumber, artist, albumTitle, genreId);

        return instanceDAO.addUsed(catalogNumber, price, format, speed,
                vinylCondition, coverCondition, defectsNotes,
                sellerFirstName, sellerLastName, sellerPhone,
                locationShelf, locationSection, locationBox);
    }

    @Override
    public Integer sellInstance(Integer instanceId, String checkNumber, String paymentMethod, BigDecimal finalPrice) {

        if (instanceId == null) {
            throw new ValidationException("Не выбран экземпляр для продажи");
        }
        if (checkNumber == null || checkNumber.trim().isEmpty()) {
            throw new ValidationException("Номер чека не может быть пустым");
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new ValidationException("Не выбран способ оплаты");
        }

        Integer saleId = instanceDAO.sell(instanceId, checkNumber, paymentMethod, finalPrice);

        if (saleId == null) {
            throw new InstanceNotAvailableException("Экземпляр не найден или уже продан");
        }

        return saleId;
    }

    @Override
    public List<Instance> searchInstances(String artist, String albumTitle, String genreName, String vinylCondition) {
        return instanceDAO.search(artist, albumTitle, genreName, vinylCondition);
    }

    @Override
    public void updateInstance(Instance instance) {
        if (instance.getInstanceId() == null) {
            throw new ValidationException("ID экземпляра не может быть пустым");
        }
        if (instance.getPrice() == null || instance.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Цена должна быть больше 0");
        }
        instanceDAO.update(instance);
    }

    @Override
    public void archiveInstance(Integer instanceId) {
        if (instanceId == null) {
            throw new ValidationException("ID экземпляра не может быть пустым");
        }
        instanceDAO.archive(instanceId);
    }
}