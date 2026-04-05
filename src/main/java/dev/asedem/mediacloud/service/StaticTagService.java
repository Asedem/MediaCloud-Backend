package dev.asedem.mediacloud.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.asedem.mediacloud.database.entity.StaticTagDefinition;
import dev.asedem.mediacloud.database.repository.StaticTagDefinitionRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StaticTagService {

    private final StaticTagDefinitionRepository staticTagDefinitionRepository;

    public StaticTagDefinition addDefinition(String title, String description) {
        return this.staticTagDefinitionRepository.save(new StaticTagDefinition(title, description));
    }

    public StaticTagDefinition updateDefinition(Integer id, String title, String description) {
        StaticTagDefinition definition = this.staticTagDefinitionRepository.findById(id).orElseThrow();
        definition.setTitle(title);
        definition.setDescription(description);
        return this.staticTagDefinitionRepository.save(definition);
    }

    public void deleteDefinition(Integer id) {
        this.staticTagDefinitionRepository.deleteById(id);
    }

    public List<StaticTagDefinition> getAllDefinitions() {
        return this.staticTagDefinitionRepository.findAll();
    }

    public StaticTagDefinition getDefinition(Integer id) {
        return this.staticTagDefinitionRepository.findById(id).orElseThrow();
    }
}
