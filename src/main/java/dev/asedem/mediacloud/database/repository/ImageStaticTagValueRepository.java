package dev.asedem.mediacloud.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.asedem.mediacloud.database.entity.ImageStaticTagValue;

@Repository
public interface ImageStaticTagValueRepository extends JpaRepository<ImageStaticTagValue, Integer> {
    List<ImageStaticTagValue> findAllByImageId(Integer imageId);
    void deleteAllByImageId(Integer imageId);
}
