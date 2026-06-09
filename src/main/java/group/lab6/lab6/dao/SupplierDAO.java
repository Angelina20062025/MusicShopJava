package group.lab6.lab6.dao;

import group.lab6.lab6.model.Supplier;

import java.util.List;

/**
 * Предоставляет методы для работы с поставщиками (юридическими лицами) в базе данных.
 */
public interface SupplierDAO {
    List<Supplier> findAll();
    Supplier save(Supplier supplier);
    Supplier update(Supplier supplier);
    void archive(Integer supplierId);
}