package group.lab6.lab6.dao;

import group.lab6.lab6.model.SellerPerson;
import java.util.List;

/**
 * Предоставляет методы для работы с продавцами (физическими лицами) в базе данных.
 */
public interface SellerPersonDAO {
    List<SellerPerson> findAll();

    SellerPerson save(SellerPerson seller);

    SellerPerson update(SellerPerson seller);

    void archive(Integer sellerId);
}