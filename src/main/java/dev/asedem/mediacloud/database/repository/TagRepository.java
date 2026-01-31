package dev.asedem.mediacloud.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.asedem.mediacloud.database.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Integer> {

}
