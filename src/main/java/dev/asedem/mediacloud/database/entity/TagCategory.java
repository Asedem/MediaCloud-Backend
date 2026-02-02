package dev.asedem.mediacloud.database.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TagCategories")
@Getter
@Setter
@NoArgsConstructor
public class TagCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Tag> tags;

    public TagCategory(Integer id, String title, List<Tag> tags) {
        this.id = id;
        this.title = title;
        this.tags = tags;
    }

    public TagCategory(String title, List<Tag> tags) {
        this.title = title;
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.setCategory(this);
    }
}
