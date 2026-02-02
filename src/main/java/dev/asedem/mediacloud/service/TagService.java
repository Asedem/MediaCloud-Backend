package dev.asedem.mediacloud.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.asedem.mediacloud.database.entity.Tag;
import dev.asedem.mediacloud.database.entity.TagCategory;
import dev.asedem.mediacloud.database.repository.TagCategoryRepository;
import dev.asedem.mediacloud.database.repository.TagRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TagService {

    public final TagRepository tagRepository;
    public final TagCategoryRepository tagCategoryRepository;

    public TagCategory addCategory(String title) {
        TagCategory category = new TagCategory(title, new ArrayList<>());
        return this.tagCategoryRepository.save(category);
    }

    public Tag addTag(String title, String description, String color, Integer categoryId) {
        TagCategory category = this.tagCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Tag tag = new Tag(title, description, color);
        category.addTag(tag);

        return this.tagRepository.save(tag);
    }

    public Tag getTagById(Integer id) {
        return this.tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
    }

    public List<TagCategory> getAllCategories() {
        return this.tagCategoryRepository.findAll();
    }
}
