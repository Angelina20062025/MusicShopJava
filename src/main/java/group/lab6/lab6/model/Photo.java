package group.lab6.lab6.model;

import java.time.LocalDateTime;

//Фотография экземпляра
public class Photo {
    private Long photoId;
    private Instance instance;
    private String photoUrl;
    private String photoDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    public Photo() {}

    public Photo(String photoUrl, String photoDescription) {
        this.photoUrl = photoUrl;
        this.photoDescription = photoDescription;
    }

    public Photo(Long photoId, Instance instance, String photoUrl,
                 String photoDescription, LocalDateTime createdAt,
                 LocalDateTime updatedAt, Boolean isDeleted) {
        this.photoId = photoId;
        this.instance = instance;
        this.photoUrl = photoUrl;
        this.photoDescription = photoDescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
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