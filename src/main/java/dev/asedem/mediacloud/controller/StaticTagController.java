package dev.asedem.mediacloud.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.asedem.mediacloud.model.StaticTagDefinitionDTO;
import dev.asedem.mediacloud.service.StaticTagService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/static-tags")
@AllArgsConstructor
public class StaticTagController {

    private final StaticTagService staticTagService;

    @PostMapping("/definitions")
    public ResponseEntity<StaticTagDefinitionDTO> addDefinition(
            @RequestParam String title,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(new StaticTagDefinitionDTO(this.staticTagService.addDefinition(title, description)));
    }

    @PutMapping("/definitions/{id}")
    public ResponseEntity<StaticTagDefinitionDTO> updateDefinition(
            @PathVariable Integer id,
            @RequestParam String title,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(new StaticTagDefinitionDTO(this.staticTagService.updateDefinition(id, title, description)));
    }

    @DeleteMapping("/definitions/{id}")
    public ResponseEntity<Void> deleteDefinition(@PathVariable Integer id) {
        this.staticTagService.deleteDefinition(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/definitions")
    public ResponseEntity<List<StaticTagDefinitionDTO>> getAllDefinitions() {
        return ResponseEntity.ok(this.staticTagService.getAllDefinitions().stream()
                .map(StaticTagDefinitionDTO::new).toList());
    }
}
