package dev.asedem.mediacloud.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "Images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "upload_path", nullable = false)
    private String uploadPath;

    @Column(name = "thumbnail_path", nullable = false)
    private String thumbnailPath;

    public Image() {}

    public Image(Integer id, String title, String uploadPath, String thumbnailPath) {
        this.id = id;
        this.title = title;
        this.uploadPath = uploadPath;
        this.thumbnailPath = thumbnailPath;
    }

    public Image(String title, String uploadPath, String thumbnailPath) {
        this.title = title;
        this.uploadPath = uploadPath;
        this.thumbnailPath = thumbnailPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
}
