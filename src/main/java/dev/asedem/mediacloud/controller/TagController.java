package dev.asedem.mediacloud.controller;

import dev.asedem.mediacloud.model.TagCategoryDTO;
import dev.asedem.mediacloud.model.TagDTO;
import dev.asedem.mediacloud.service.TagService;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@AllArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping("/categories")
    public ResponseEntity<TagCategoryDTO> addCategory(@RequestParam String title) {
        return ResponseEntity.ok(new TagCategoryDTO(this.tagService.addCategory(title)));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<TagCategoryDTO> updateCategory(@PathVariable Integer id, @RequestParam String title) {
        return ResponseEntity.ok(new TagCategoryDTO(this.tagService.updateCategory(id, title)));
    }

    @PostMapping
    public ResponseEntity<TagDTO> addTag(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String color,
            @RequestParam Integer categoryId) {
        return ResponseEntity.ok(new TagDTO(this.tagService.addTag(title, description, color, categoryId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(
            @PathVariable Integer id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String color,
            @RequestParam Integer categoryId) {
        return ResponseEntity.ok(new TagDTO(this.tagService.updateTag(id, title, description, color, categoryId)));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<TagCategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(this.tagService
                .getAllCategories()
                .stream()
                .map(TagCategoryDTO::new)
                .toList());
    }
}