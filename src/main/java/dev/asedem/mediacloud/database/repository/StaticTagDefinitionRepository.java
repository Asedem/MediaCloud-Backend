package dev.asedem.mediacloud.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.asedem.mediacloud.database.entity.StaticTagDefinition;

@Repository
public interface StaticTagDefinitionRepository extends JpaRepository<StaticTagDefinition, Integer> {
}
