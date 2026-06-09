package group.lab6.lab6.service;

import group.lab6.lab6.model.SellerPerson;
import group.lab6.lab6.model.Supplier;
import java.util.List;

/**
 * Содержит бизнес-логику работы со справочными данными (поставщики и продавцы).
 * Обеспечивает CRUD-операции для этих сущностей.
 */
public interface ReferenceService {

    List<Supplier> getAllSuppliers();

    Supplier addSupplier(String name, String contactInfo);

    Supplier updateSupplier(Supplier supplier);

    void archiveSupplier(Integer supplierId);

    List<SellerPerson> getAllSellers();
    SellerPerson addSeller(String firstName, String lastName, String patronymic, String phone);
    SellerPerson updateSeller(SellerPerson seller);
    void archiveSeller(Integer sellerId);
}