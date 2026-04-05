package dev.asedem.mediacloud.database.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "Images")
@Getter
@Setter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "image_tags", joinColumns = @JoinColumn(name = "image_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new java.util.HashSet<>();

    @jakarta.persistence.OneToMany(mappedBy = "image", fetch = FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL)
    private java.util.List<ImageStaticTagValue> staticTagValues = new java.util.ArrayList<>();

    public Image(Integer id, String title, String name) {
        this.id = id;
        this.title = title;
        this.name = name;
    }

    public Image(String title, String name) {
        this.title = title;
        this.name = name;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getImages().add(this);
    }
}
