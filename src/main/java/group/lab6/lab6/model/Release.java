package group.lab6.lab6.model;

import java.time.LocalDateTime;

public class Release {
    private Long releaseId;
    private String catalogNumber;
    private Genre genre;
    private String artist;
    private String albumTitle;
    private String label;
    private String country;
    private Integer releaseYear;
    private String description;
    private Integer numberOfCopies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    public Release() {}

    public Release(String catalogNumber, String artist, String albumTitle) {
        this.catalogNumber = catalogNumber;
        this.artist = artist;
        this.albumTitle = albumTitle;
        this.numberOfCopies = 0;
    }

    public Release(Long releaseId, String catalogNumber, Genre genre, String artist,
                   String albumTitle, String label, String country, Integer releaseYear,
                   String description, Integer numberOfCopies, LocalDateTime createdAt,
                   LocalDateTime updatedAt, Boolean isDeleted) {
        this.releaseId = releaseId;
        this.catalogNumber = catalogNumber;
        this.genre = genre;
        this.artist = artist;
        this.albumTitle = albumTitle;
        this.label = label;
        this.country = country;
        this.releaseYear = releaseYear;
        this.description = description;
        this.numberOfCopies = numberOfCopies;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    public Long getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(Long releaseId) {
        this.releaseId = releaseId;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(Integer numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
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

    public String getFullTitle() {
        return artist + " - " + albumTitle;
    }

    @Override
    public String toString() {
        return getFullTitle() + " (" + catalogNumber + ")";
    }
}