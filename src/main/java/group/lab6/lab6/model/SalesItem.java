package group.lab6.lab6.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//Позиция в чеке продажи
public class SalesItem {
    private Long idSalesItems;
    private Sale sale;
    private Instance instance;
    private BigDecimal price;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    public SalesItem() {}

    public SalesItem(Instance instance, BigDecimal price, Integer quantity) {
        this.instance = instance;
        this.price = price;
        this.quantity = quantity;
    }

    public SalesItem(Long idSalesItems, Sale sale, Instance instance,
                     BigDecimal price, Integer quantity, LocalDateTime createdAt,
                     LocalDateTime updatedAt, Boolean isDeleted) {
        this.idSalesItems = idSalesItems;
        this.sale = sale;
        this.instance = instance;
        this.price = price;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    public Long getIdSalesItems() {
        return idSalesItems;
    }

    public void setIdSalesItems(Long idSalesItems) {
        this.idSalesItems = idSalesItems;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public BigDecimal getTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return instance.getRelease().getFullTitle() + " x" + quantity + " = " + getTotal() + " ₽";
    }
}