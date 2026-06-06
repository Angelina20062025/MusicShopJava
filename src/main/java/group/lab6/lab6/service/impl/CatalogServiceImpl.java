package group.lab6.lab6.service.impl;

import group.lab6.lab6.dao.ReleaseDAO;
import group.lab6.lab6.dao.InstanceDAO;
import group.lab6.lab6.model.Release;
import group.lab6.lab6.model.Genre;
import group.lab6.lab6.service.CatalogService;
import group.lab6.lab6.service.exceptions.ValidationException;

import java.util.List;
import java.util.Optional;

public class CatalogServiceImpl implements CatalogService {

    private final ReleaseDAO releaseDAO;
    private final InstanceDAO instanceDAO;

    public CatalogServiceImpl(ReleaseDAO releaseDAO, InstanceDAO instanceDAO) {
        this.releaseDAO = releaseDAO;
        this.instanceDAO = instanceDAO;
    }

    @Override
    public Integer addOrGetRelease(String catalogNumber, String artist, String albumTitle, Integer genreId) {
        if (catalogNumber == null || catalogNumber.trim().isEmpty()) {
            throw new ValidationException("Каталоговый номер не может быть пустым");
        }
        if (artist == null || artist.trim().isEmpty()) {
            throw new ValidationException("Исполнитель не может быть пустым");
        }
        if (albumTitle == null || albumTitle.trim().isEmpty()) {
            throw new ValidationException("Название альбома не может быть пустым");
        }

        return releaseDAO.addOrGet(catalogNumber, artist, albumTitle, genreId, null, null, null, null);
    }

    @Override
    public Optional<Release> findByCatalogNumber(String catalogNumber) {
        if (catalogNumber == null || catalogNumber.trim().isEmpty()) {
            throw new ValidationException("Каталоговый номер не может быть пустым");
        }
        return releaseDAO.findByCatalogNumber(catalogNumber);
    }

    @Override
    public List<Release> searchReleases(String artist, String albumTitle, String genreName) {
        return releaseDAO.search(artist, albumTitle, genreName);
    }

    @Override
    public void updateRelease(Release release) {
        if (release.getReleaseId() == null) {
            throw new ValidationException("ID релиза не может быть пустым");
        }
        if (release.getArtist() == null || release.getArtist().trim().isEmpty()) {
            throw new ValidationException("Исполнитель не может быть пустым");
        }
        if (release.getAlbumTitle() == null || release.getAlbumTitle().trim().isEmpty()) {
            throw new ValidationException("Название альбома не может быть пустым");
        }

        releaseDAO.update(release);
    }

    @Override
    public void archiveRelease(Integer releaseId) {
        if (releaseId == null) {
            throw new ValidationException("ID релиза не может быть пустым");
        }
        releaseDAO.archive(releaseId);
    }

    @Override
    public List<Genre> getAllGenres() {
        return instanceDAO.getAllGenres();
    }
}