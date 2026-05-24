package group.lab6.lab6.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Детали для Б/У экземпляра (состояние, дефекты, продавец, фото)
public class UsedDetails {
    private Long usedDetailsId;
    private SellerPerson sellerPerson;
    private ConditionGrade vinylCondition;
    private ConditionGrade coverCondition;
    private String defectsNotes;
    private List<DefectPhoto> defectPhotos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    public UsedDetails() {
        this.defectPhotos = new ArrayList<>();
    }

    public UsedDetails(SellerPerson sellerPerson, ConditionGrade vinylCondition,
                       ConditionGrade coverCondition, String defectsNotes) {
        this();
        this.sellerPerson = sellerPerson;
        this.vinylCondition = vinylCondition;
        this.coverCondition = coverCondition;
        this.defectsNotes = defectsNotes;
    }

    public Long getUsedDetailsId() {
        return usedDetailsId;
    }

    public void setUsedDetailsId(Long usedDetailsId) {
        this.usedDetailsId = usedDetailsId;
    }

    public SellerPerson getSellerPerson() {
        return sellerPerson;
    }

    public void setSellerPerson(SellerPerson sellerPerson) {
        this.sellerPerson = sellerPerson;
    }

    public ConditionGrade getVinylCondition() {
        return vinylCondition;
    }

    public void setVinylCondition(ConditionGrade vinylCondition) {
        this.vinylCondition = vinylCondition;
    }

    public ConditionGrade getCoverCondition() {
        return coverCondition;
    }

    public void setCoverCondition(ConditionGrade coverCondition) {
        this.coverCondition = coverCondition;
    }

    public String getDefectsNotes() {
        return defectsNotes;
    }

    public void setDefectsNotes(String defectsNotes) {
        this.defectsNotes = defectsNotes;
    }

    public List<DefectPhoto> getDefectPhotos() {
        return defectPhotos;
    }

    public void setDefectPhotos(List<DefectPhoto> defectPhotos) {
        this.defectPhotos = defectPhotos;
    }

    public void addDefectPhoto(DefectPhoto photo) {
        this.defectPhotos.add(photo);
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
}