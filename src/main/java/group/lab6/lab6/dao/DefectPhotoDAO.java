package group.lab6.lab6.dao;

import group.lab6.lab6.model.DefectPhoto;

import java.util.List;

public interface DefectPhotoDAO {
    List<DefectPhoto> findByUsedDetailsId(Integer usedDetailsId);
    Integer addDefectPhoto(Integer usedDetailsId, String photoUrl, String description);
    void updateDescription(Integer defectPhotoId, String description);
    void updateUrl(Integer defectPhotoId, String photoUrl);
    void archive(Integer defectPhotoId);
}