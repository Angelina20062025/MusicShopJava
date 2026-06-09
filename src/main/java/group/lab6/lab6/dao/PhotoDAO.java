package group.lab6.lab6.dao;

import group.lab6.lab6.model.Photo;

import java.util.List;

/**
 * Предоставляет методы для работы с фотографиями экземпляров в базе данных.
 */
public interface PhotoDAO {
    List<Photo> findByInstanceId(Integer instanceId);
    Integer addPhoto(Integer instanceId, String photoUrl, String description);
    void updateDescription(Integer photoId, String description);
    void updateUrl(Integer photoId, String photoUrl);
    void archive(Integer photoId);
}