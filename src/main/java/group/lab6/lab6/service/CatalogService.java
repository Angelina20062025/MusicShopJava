package group.lab6.lab6.service;

import group.lab6.lab6.model.Release;
import group.lab6.lab6.model.Genre;
import java.util.List;
import java.util.Optional;

public interface CatalogService {

    Integer addOrGetRelease(String catalogNumber, String artist, String albumTitle, Integer genreId);

    Optional<Release> findByCatalogNumber(String catalogNumber);

    List<Release> searchReleases(String artist, String albumTitle, String genreName);

    void updateRelease(Release release);

    void archiveRelease(Integer releaseId);

    List<Genre> getAllGenres();
}