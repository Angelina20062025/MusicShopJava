package group.lab6.lab6.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Продажа
public class Sale {
    private Long saleId;
    private LocalDateTime saleDate;
    private String checkNumber;
    private PaymentMethod paymentMethod;
    private List<SalesItem> salesItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    public Sale() {
        this.salesItems = new ArrayList<>();
    }

    public Sale(String checkNumber, PaymentMethod paymentMethod) {
        this();
        this.checkNumber = checkNumber;
        this.paymentMethod = paymentMethod;
        this.saleDate = LocalDateTime.now();
    }

    public Sale(Long saleId, LocalDateTime saleDate, String checkNumber,
                PaymentMethod paymentMethod, LocalDateTime createdAt,
                LocalDateTime updatedAt, Boolean isDeleted) {
        this();
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.checkNumber = checkNumber;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters
    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<SalesItem> getSalesItems() {
        return salesItems;
    }

    public void setSalesItems(List<SalesItem> salesItems) {
        this.salesItems = salesItems;
    }

    public void addSalesItem(SalesItem item) {
        this.salesItems.add(item);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public BigDecimal getTotalAmount() {
        return salesItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return "Чек №" + checkNumber + " от " + saleDate + " на сумму " + getTotalAmount() + " ₽";
    }
}