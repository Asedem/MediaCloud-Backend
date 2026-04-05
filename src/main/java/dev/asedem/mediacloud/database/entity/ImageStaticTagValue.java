package dev.asedem.mediacloud.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ImageStaticTagValues")
@Getter
@Setter
@NoArgsConstructor
public class ImageStaticTagValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "static_tag_definition_id", nullable = false)
    private StaticTagDefinition staticTagDefinition;

    @Column(name = "value", nullable = false)
    private Double value;

    public ImageStaticTagValue(Image image, StaticTagDefinition staticTagDefinition, Double value) {
        this.image = image;
        this.staticTagDefinition = staticTagDefinition;
        this.value = value;
    }
}
