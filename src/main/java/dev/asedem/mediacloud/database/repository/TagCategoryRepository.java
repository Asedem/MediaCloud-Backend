package dev.asedem.mediacloud.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.asedem.mediacloud.database.entity.TagCategory;

public interface TagCategoryRepository extends JpaRepository<TagCategory, Integer> {

}
