package group.lab6.lab6.dao;

import group.lab6.lab6.model.DefectPhoto;

import java.util.List;

/**
 * Предоставляет методы для работы с фотографиями дефектов Б/У экземпляров в базе данных.
 */
public interface DefectPhotoDAO {
    /**
     * Возвращает все фотографии дефектов для указанной записи UsedDetails.
     * @param usedDetailsId идентификатор записи UsedDetails (не может быть null)
     * @return список фотографий дефектов, может быть пустым, если фото нет
     */
    List<DefectPhoto> findByUsedDetailsId(Integer usedDetailsId);
    /**
     * Добавляет новую фотографию дефекта.
     * @param usedDetailsId идентификатор записи UsedDetails (не может быть null)
     * @param photoUrl путь к файлу фотографии (не может быть null или пустым)
     * @param description описание фотографии (может быть null)
     * @return идентификатор созданной фотографии дефекта
     */
    Integer addDefectPhoto(Integer usedDetailsId, String photoUrl, String description);
    /**
     * Обновляет описание фотографии дефекта.
     * @param defectPhotoId идентификатор фотографии дефекта (не может быть null)
     * @param description новое описание (может быть null)
     */
    void updateDescription(Integer defectPhotoId, String description);
    /**
     * Обновляет URL (путь к файлу) фотографии дефекта.
     * @param defectPhotoId идентификатор фотографии дефекта (не может быть null)
     * @param photoUrl новый путь к файлу (не может быть null или пустым)
     */
    void updateUrl(Integer defectPhotoId, String photoUrl);
    /**
     * Выполняет мягкое удаление фотографии дефекта (устанавливает is_deleted = TRUE).
     * @param defectPhotoId идентификатор фотографии дефекта (не может быть null)
     */
    void archive(Integer defectPhotoId);
}