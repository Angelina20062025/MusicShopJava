package group.lab6.lab6.service.impl;

import group.lab6.lab6.dao.SaleDAO;
import group.lab6.lab6.service.ReportService;
import group.lab6.lab6.service.exceptions.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportServiceImpl implements ReportService {

    private final SaleDAO saleDAO;

    public ReportServiceImpl(SaleDAO saleDAO) {
        this.saleDAO = saleDAO;
    }

    @Override
    public List<Map<String, Object>> getSoldReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new ValidationException("Дата начала не может быть пустой");
        }
        if (endDate == null) {
            throw new ValidationException("Дата окончания не может быть пустой");
        }
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Дата начала не может быть позже даты окончания");
        }

        return saleDAO.getSoldReport(startDate, endDate);
    }

    @Override
    public BigDecimal getCollectionValue() {
        return saleDAO.getCollectionValue();
    }
}