package group.lab6.lab6.service.impl;

import group.lab6.lab6.dao.SellerPersonDAO;
import group.lab6.lab6.dao.SupplierDAO;
import group.lab6.lab6.model.SellerPerson;
import group.lab6.lab6.model.Supplier;
import group.lab6.lab6.service.ReferenceService;
import group.lab6.lab6.service.exceptions.ValidationException;

import java.util.List;

public class ReferenceServiceImpl implements ReferenceService {

    private final SupplierDAO supplierDAO;
    private final SellerPersonDAO sellerPersonDAO;

    public ReferenceServiceImpl(SupplierDAO supplierDAO, SellerPersonDAO sellerPersonDAO) {
        this.supplierDAO = supplierDAO;
        this.sellerPersonDAO = sellerPersonDAO;
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
        return supplierDAO.update(supplier);
    }

    @Override
    public void archiveSupplier(Integer supplierId) {
        if (supplierId == null) {
            throw new ValidationException("ID поставщика не может быть пустым");
        }
        supplierDAO.archive(supplierId);
    }

    @Override
    public List<SellerPerson> getAllSellers() {
        return sellerPersonDAO.findAll();
    }

    @Override
    public SellerPerson addSeller(String firstName, String lastName, String patronymic, String phone) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("Имя продавца не может быть пустым");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("Фамилия продавца не может быть пустой");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Телефон продавца не может быть пустым");
        }
        SellerPerson seller = new SellerPerson();
        seller.setFirstName(firstName);
        seller.setLastName(lastName);
        seller.setPatronymic(patronymic);
        seller.setPhone(phone);
        return sellerPersonDAO.save(seller);
    }

    @Override
    public SellerPerson updateSeller(SellerPerson seller) {
        if (seller.getPersonId() == null) {
            throw new ValidationException("ID продавца не может быть пустым");
        }
        return sellerPersonDAO.update(seller);
    }

    @Override
    public void archiveSeller(Integer sellerId) {
        if (sellerId == null) {
            throw new ValidationException("ID продавца не может быть пустым");
        }
        sellerPersonDAO.archive(sellerId);
    }
}