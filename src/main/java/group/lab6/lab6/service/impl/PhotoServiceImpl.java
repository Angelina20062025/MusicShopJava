package group.lab6.lab6.service.impl;

import group.lab6.lab6.dao.PhotoDAO;
import group.lab6.lab6.dao.DefectPhotoDAO;
import group.lab6.lab6.model.Photo;
import group.lab6.lab6.model.DefectPhoto;
import group.lab6.lab6.service.PhotoService;
import group.lab6.lab6.service.exceptions.ValidationException;

import java.util.List;

public class PhotoServiceImpl implements PhotoService {

    private final PhotoDAO photoDAO;
    private final DefectPhotoDAO defectPhotoDAO;

    public PhotoServiceImpl(PhotoDAO photoDAO, DefectPhotoDAO defectPhotoDAO) {
        this.photoDAO = photoDAO;
        this.defectPhotoDAO = defectPhotoDAO;
    }

    @Override
    public List<Photo> getPhotosByInstanceId(Integer instanceId) {
        if (instanceId == null) {
            throw new ValidationException("ID экземпляра не может быть пустым");
        }
        return photoDAO.findByInstanceId(instanceId);
    }

    @Override
    public Integer addPhoto(Integer instanceId, String photoUrl, String description) {
        if (instanceId == null) {
            throw new ValidationException("ID экземпляра не может быть пустым");
        }
        if (photoUrl == null || photoUrl.trim().isEmpty()) {
            throw new ValidationException("URL фото не может быть пустым");
        }

        return photoDAO.addPhoto(instanceId, photoUrl, description);
    }

    @Override
    public void updatePhotoDescription(Integer photoId, String description) {
        if (photoId == null) {
            throw new ValidationException("ID фото не может быть пустым");
        }
        photoDAO.updateDescription(photoId, description);
    }

    @Override
    public void updatePhotoUrl(Integer photoId, String photoUrl) {
        if (photoId == null) {
            throw new ValidationException("ID фото не может быть пустым");
        }
        if (photoUrl == null || photoUrl.trim().isEmpty()) {
            throw new ValidationException("URL фото не может быть пустым");
        }
        photoDAO.updateUrl(photoId, photoUrl);
    }

    @Override
    public void archivePhoto(Integer photoId) {
        if (photoId == null) {
            throw new ValidationException("ID фото не может быть пустым");
        }
        photoDAO.archive(photoId);
    }

    @Override
    public List<DefectPhoto> getDefectPhotosByUsedDetailsId(Integer usedDetailsId) {
        if (usedDetailsId == null) {
            throw new ValidationException("ID UsedDetails не может быть пустым");
        }
        return defectPhotoDAO.findByUsedDetailsId(usedDetailsId);
    }

    @Override
    public Integer addDefectPhoto(Integer usedDetailsId, String photoUrl, String description) {
        if (usedDetailsId == null) {
            throw new ValidationException("ID UsedDetails не может быть пустым");
        }
        if (photoUrl == null || photoUrl.trim().isEmpty()) {
            throw new ValidationException("URL фото не может быть пустым");
        }

        return defectPhotoDAO.addDefectPhoto(usedDetailsId, photoUrl, description);
    }

    @Override
    public void updateDefectPhotoDescription(Integer defectPhotoId, String description) {
        if (defectPhotoId == null) {
            throw new ValidationException("ID фото дефекта не может быть пустым");
        }
        defectPhotoDAO.updateDescription(defectPhotoId, description);
    }

    @Override
    public void updateDefectPhotoUrl(Integer defectPhotoId, String photoUrl) {
        if (defectPhotoId == null) {
            throw new ValidationException("ID фото дефекта не может быть пустым");
        }
        if (photoUrl == null || photoUrl.trim().isEmpty()) {
            throw new ValidationException("URL фото не может быть пустым");
        }
        defectPhotoDAO.updateUrl(defectPhotoId, photoUrl);
    }

    @Override
    public void archiveDefectPhoto(Integer defectPhotoId) {
        if (defectPhotoId == null) {
            throw new ValidationException("ID фото дефекта не может быть пустым");
        }
        defectPhotoDAO.archive(defectPhotoId);
    }
}