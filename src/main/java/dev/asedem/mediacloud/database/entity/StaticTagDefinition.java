package dev.asedem.mediacloud.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "StaticTagDefinitions")
@Getter
@Setter
@NoArgsConstructor
public class StaticTagDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    public StaticTagDefinition(Integer id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public StaticTagDefinition(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
