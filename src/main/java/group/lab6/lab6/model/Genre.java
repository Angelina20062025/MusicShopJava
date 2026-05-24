package group.lab6.lab6.model;

import java.time.LocalDateTime;

public class Genre {
    private Long genreId;
    private String genreName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    //конструктор по умолчанию
    public Genre() {}

    //конструктор с обязательными полями
    public Genre(String genreName) {
        this.genreName = genreName;
    }

    public Genre(Long genreId, String genreName, LocalDateTime createdAt,
                 LocalDateTime updatedAt, Boolean isDeleted) {
        this.genreId = genreId;
        this.genreName = genreName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
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

    @Override
    public String toString() {
        return genreName;
    }
}