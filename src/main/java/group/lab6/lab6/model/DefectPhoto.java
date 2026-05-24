package group.lab6.lab6.model;

import java.time.LocalDateTime;

//Фотография дефекта для Б/У экземпляра
public class DefectPhoto {
    private Long defectPhotoId;
    private UsedDetails usedDetails;
    private String photoUrl;
    private String photoDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    public DefectPhoto() {}

    public DefectPhoto(String photoUrl, String photoDescription) {
        this.photoUrl = photoUrl;
        this.photoDescription = photoDescription;
    }

    public DefectPhoto(Long defectPhotoId, UsedDetails usedDetails, String photoUrl,
                       String photoDescription, LocalDateTime createdAt,
                       LocalDateTime updatedAt, Boolean isDeleted) {
        this.defectPhotoId = defectPhotoId;
        this.usedDetails = usedDetails;
        this.photoUrl = photoUrl;
        this.photoDescription = photoDescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    public Long getDefectPhotoId() {
        return defectPhotoId;
    }

    public void setDefectPhotoId(Long defectPhotoId) {
        this.defectPhotoId = defectPhotoId;
    }

    public UsedDetails getUsedDetails() {
        return usedDetails;
    }

    public void setUsedDetails(UsedDetails usedDetails) {
        this.usedDetails = usedDetails;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoDescription() {
        return photoDescription;
    }

    public void setPhotoDescription(String photoDescription) {
        this.photoDescription = photoDescription;
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

    public String getFileName() {
        if (photoUrl != null && photoUrl.contains("/")) {
            return photoUrl.substring(photoUrl.lastIndexOf("/") + 1);
        }
        return photoUrl;
    }

    @Override
    public String toString() {
        return photoDescription != null ? photoDescription : getFileName();
    }
}