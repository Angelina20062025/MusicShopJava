package group.lab6.lab6.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportService {

    List<Map<String, Object>> getSoldReport(LocalDate startDate, LocalDate endDate);

    BigDecimal getCollectionValue();
}
