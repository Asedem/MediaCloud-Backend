package dev.asedem.mediacloud.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.asedem.mediacloud.database.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Integer> {

}
