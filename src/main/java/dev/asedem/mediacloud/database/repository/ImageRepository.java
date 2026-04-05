package dev.asedem.mediacloud.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.asedem.mediacloud.database.entity.Image;
import java.util.List;
import java.util.Set;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    @Query("SELECT DISTINCT i FROM Image i " +
            "LEFT JOIN i.tags t " +
            "WHERE (cast(:tagIds as org.hibernate.type.descriptor.java.IntegerJavaType) IS NULL OR t.id IN :tagIds) " +
            "AND (cast(:title as String) IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', cast(:title as String), '%')))")
    List<Image> findImagesByTagsAndTitle(
            @Param("tagIds") Set<Integer> tagIds,
            @Param("title") String title);

    @Query("SELECT i FROM Image i " +
            "JOIN i.tags t " +
            "WHERE (t.id IN :tagIds) " +
            "AND (cast(:title as String) IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', cast(:title as String), '%'))) " +
            "GROUP BY i.id " +
            "HAVING COUNT(DISTINCT t.id) = cast(:tagCount as long)")
    List<Image> findImagesByAllTagsAndTitle(
            @Param("tagIds") Set<Integer> tagIds,
            @Param("title") String title,
            @Param("tagCount") Integer tagCount);
}
