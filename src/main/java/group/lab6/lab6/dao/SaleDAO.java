package group.lab6.lab6.dao;

import group.lab6.lab6.model.Sale;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SaleDAO {
    List<Map<String, Object>> getSoldReport(LocalDate startDate, LocalDate endDate);
    BigDecimal getCollectionValue();
}