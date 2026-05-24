package group.lab6.lab6.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Instance {
    private Long instanceId;
    private Release release;
    private BigDecimal price;
    private String locationShelf;
    private String locationSection;
    private String locationBox;
    private InstanceStatus status;
    private FormatType format;
    private SpeedType speed;
    private Supplier supplier;
    private UsedDetails usedDetails;
    private List<Photo> photos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    public Instance() {
        this.photos = new ArrayList<>();
    }

    public Instance(Release release, BigDecimal price, FormatType format, SpeedType speed) {
        this();
        this.release = release;
        this.price = price;
        this.format = format;
        this.speed = speed;
        this.status = InstanceStatus.IN_STOCK;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getLocationShelf() {
        return locationShelf;
    }

    public void setLocationShelf(String locationShelf) {
        this.locationShelf = locationShelf;
    }

    public String getLocationSection() {
        return locationSection;
    }

    public void setLocationSection(String locationSection) {
        this.locationSection = locationSection;
    }

    public String getLocationBox() {
        return locationBox;
    }

    public void setLocationBox(String locationBox) {
        this.locationBox = locationBox;
    }

    public InstanceStatus getStatus() {
        return status;
    }

    public void setStatus(InstanceStatus status) {
        this.status = status;
    }

    public FormatType getFormat() {
        return format;
    }

    public void setFormat(FormatType format) {
        this.format = format;
    }

    public SpeedType getSpeed() {
        return speed;
    }

    public void setSpeed(SpeedType speed) {
        this.speed = speed;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public UsedDetails getUsedDetails() {
        return usedDetails;
    }

    public void setUsedDetails(UsedDetails usedDetails) {
        this.usedDetails = usedDetails;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public void addPhoto(Photo photo) {
        this.photos.add(photo);
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

    public String getFullLocation() {
        StringBuilder sb = new StringBuilder();
        if (locationShelf != null) sb.append("Стеллаж: ").append(locationShelf);
        if (locationSection != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Секция: ").append(locationSection);
        }
        if (locationBox != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Коробка: ").append(locationBox);
        }
        return sb.length() > 0 ? sb.toString() : "Не указано";
    }

    public boolean isNew() {
        return supplier != null && usedDetails == null;
    }

    public boolean isUsed() {
        return usedDetails != null && supplier == null;
    }

    public String getDisplayText() {
        return String.format("%s - %s | Состояние: %s | Цена: %.2f ₽ | Место: %s",
                release.getArtist(),
                release.getAlbumTitle(),
                isUsed() && usedDetails.getVinylCondition() != null ? usedDetails.getVinylCondition().getRusName() : "новый",
                price,
                getFullLocation()
        );
    }

    @Override
    public String toString() {
        return getDisplayText();
    }
}