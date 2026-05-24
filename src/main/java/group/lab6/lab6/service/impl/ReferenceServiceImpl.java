package group.lab6.lab6.service.impl;

import group.lab6.lab6.dao.SupplierDAO;
import group.lab6.lab6.model.Supplier;
import group.lab6.lab6.service.ReferenceService;
import group.lab6.lab6.service.exceptions.ValidationException;

import java.util.List;

public class ReferenceServiceImpl implements ReferenceService {

    private final SupplierDAO supplierDAO;

    public ReferenceServiceImpl(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierDAO.findAll();
    }

    @Override
    public Supplier addSupplier(String name, String contactInfo) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Название поставщика не может быть пустым");
        }

        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setContactInfo(contactInfo);

        return supplierDAO.save(supplier);
    }

    @Override
    public Supplier updateSupplier(Supplier supplier) {
        if (supplier.getSupplierId() == null) {
            throw new ValidationException("ID поставщика не может быть пустым");
        }
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new ValidationException("Название поставщика не может быть пустым");
        }

        return supplierDAO.update(supplier);
    }

    @Override
    public void archiveSupplier(Integer supplierId) {
        if (supplierId == null) {
            throw new ValidationException("ID поставщика не может быть пустым");
        }
        supplierDAO.archive(supplierId);
    }
}