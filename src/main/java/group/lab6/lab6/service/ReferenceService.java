package group.lab6.lab6.service;

import group.lab6.lab6.model.Supplier;
import java.util.List;

public interface ReferenceService {

    List<Supplier> getAllSuppliers();

    Supplier addSupplier(String name, String contactInfo);

    Supplier updateSupplier(Supplier supplier);

    void archiveSupplier(Integer supplierId);
}