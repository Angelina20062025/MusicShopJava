package group.lab6.lab6.dao;

import group.lab6.lab6.model.Release;

import java.util.List;
import java.util.Optional;

/**
 * Предоставляет методы для работы с релизами в базе данных.
 */
public interface ReleaseDAO {
    Release update(Release release);

    Integer addOrGet(String catalogNumber, String artist, String albumTitle, Integer genreId,
                     String label, String country, Integer releaseYear, String description);
    Optional<Release> findByCatalogNumber(String catalogNumber);
    List<Release> search(String artist, String albumTitle, String genreName);
    Optional<Release> findById(Long id);
    void archive(Integer releaseId);
}