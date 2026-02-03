package dev.asedem.mediacloud.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.asedem.mediacloud.database.entity.Image;
import dev.asedem.mediacloud.database.entity.Tag;
import java.util.List;
import java.util.Set;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    @Query("SELECT DISTINCT i FROM Image i JOIN i.tags t WHERE t IN :tags")
    List<Image> findImagesByAnyTag(@Param("tags") Set<Tag> tags);
}
