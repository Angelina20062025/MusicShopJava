package group.lab6.lab6.service;

import group.lab6.lab6.model.Photo;
import group.lab6.lab6.model.DefectPhoto;
import java.util.List;

/**
 * Содержит бизнес-логику работы с фотографиями (обычными и дефектов).
 * Фотографии физически хранятся в папке пользователя, а в БД сохраняется путь.
 */
public interface PhotoService {

    List<Photo> getPhotosByInstanceId(Integer instanceId);

    Integer addPhoto(Integer instanceId, String photoUrl, String description);

    void updatePhotoDescription(Integer photoId, String description);

    void updatePhotoUrl(Integer photoId, String photoUrl);

    void archivePhoto(Integer photoId);

    List<DefectPhoto> getDefectPhotosByUsedDetailsId(Integer usedDetailsId);

    Integer addDefectPhoto(Integer usedDetailsId, String photoUrl, String description);

    void updateDefectPhotoDescription(Integer defectPhotoId, String description);

    void updateDefectPhotoUrl(Integer defectPhotoId, String photoUrl);

    void archiveDefectPhoto(Integer defectPhotoId);
}