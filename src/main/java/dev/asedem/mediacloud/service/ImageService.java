package dev.asedem.mediacloud.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.sksamuel.scrimage.ImmutableImage;

import dev.asedem.mediacloud.database.entity.Image;
import dev.asedem.mediacloud.database.entity.StaticTagDefinition;
import dev.asedem.mediacloud.database.entity.ImageStaticTagValue;
import dev.asedem.mediacloud.database.entity.Tag;
import dev.asedem.mediacloud.database.repository.ImageRepository;
import dev.asedem.mediacloud.database.repository.ImageStaticTagValueRepository;
import jakarta.transaction.Transactional;

@Service
public class ImageService {

    private static final String UPLOAD_DIR = "./stored_images/";
    private static final String THUMB_DIR = "./stored_thumbnails/";
    private static final String ALGORITHM = "AES";
    private static final String KEY = "e8519eb540c73be13b8d91439089f6c152ecf23f477cbd1dd4a5c4a838e37188";

    private final ImageRepository imageRepository;
    private final TagService tagService;
    private final StaticTagService staticTagService;
    private final dev.asedem.mediacloud.database.repository.ImageStaticTagValueRepository imageStaticTagValueRepository;
    private final jakarta.persistence.EntityManager entityManager;

    public ImageService(ImageRepository imageRepository, TagService tagService, StaticTagService staticTagService, dev.asedem.mediacloud.database.repository.ImageStaticTagValueRepository imageStaticTagValueRepository, jakarta.persistence.EntityManager entityManager) {
        new File(UPLOAD_DIR).mkdirs();
        new File(THUMB_DIR).mkdirs();
        this.imageRepository = imageRepository;
        this.tagService = tagService;
        this.staticTagService = staticTagService;
        this.imageStaticTagValueRepository = imageStaticTagValueRepository;
        this.entityManager = entityManager;
    }

    public Image uploadImage(String title, MultipartFile file) throws Exception {
        byte[] fileBytes = file.getBytes();
        return processAndSaveImage(title, fileBytes);
    }

    public Image uploadImageFromUrl(String title, String imageUrl) throws Exception {
        byte[] imageBytes = new RestTemplate()
                .getForObject(imageUrl, byte[].class);

        if (imageBytes == null) {
            throw new RuntimeException("Could not download image from the provided source");
        }

        return this.processAndSaveImage(title, imageBytes);
    }

    private Image processAndSaveImage(String title, byte[] fileBytes) throws Exception {
        String uuid = UUID.randomUUID().toString();

        this.saveEncryptedFile(fileBytes, UPLOAD_DIR, uuid);
        this.saveEncryptedThumbnail(fileBytes, uuid);

        Image image = new Image(title, uuid);
        return this.imageRepository.save(image);
    }

    public List<Image> getAllImages() {
        return this.imageRepository.findAll();
    }

    public List<Image> getFilteredImages(List<Tag> tags, String title, String filterMode, Map<Integer, dev.asedem.mediacloud.model.RangeFilterDTO> staticTagFilters, int page, int size, String sortBy, String sortDirection) {
        StringBuilder jpql = new StringBuilder("SELECT i FROM Image i ");
        boolean exactMode = "exact".equalsIgnoreCase(filterMode) && tags != null && !tags.isEmpty();
        
        if (tags != null && !tags.isEmpty()) {
            jpql.append("JOIN i.tags t ");
        }

        boolean sortIsStatic = sortBy != null && sortBy.startsWith("static_");
        if (sortIsStatic) {
            jpql.append("LEFT JOIN i.staticTagValues sv_sort ON sv_sort.staticTagDefinition.id = :sortDefId ");
        }

        jpql.append("WHERE 1=1 ");

        if (title != null && !title.isBlank()) {
            jpql.append("AND LOWER(i.title) LIKE LOWER(:title) ");
        }

        if (tags != null && !tags.isEmpty()) {
            jpql.append("AND t.id IN :tagIds ");
        }

        if (staticTagFilters != null && !staticTagFilters.isEmpty()) {
            int filterIdx = 0;
            for (Integer defId : staticTagFilters.keySet()) {
                jpql.append("AND EXISTS (SELECT 1 FROM ImageStaticTagValue sv_f").append(filterIdx)
                    .append(" WHERE sv_f").append(filterIdx).append(".image = i")
                    .append(" AND sv_f").append(filterIdx).append(".staticTagDefinition.id = :defId_").append(filterIdx);
                
                dev.asedem.mediacloud.model.RangeFilterDTO filter = staticTagFilters.get(defId);
                if (filter.min() != null) {
                    jpql.append(" AND sv_f").append(filterIdx).append(".value >= :min_").append(filterIdx);
                }
                if (filter.max() != null) {
                    jpql.append(" AND sv_f").append(filterIdx).append(".value <= :max_").append(filterIdx);
                }
                jpql.append(") ");
                filterIdx++;
            }
        }

        if (exactMode) {
            jpql.append("GROUP BY i.id ");
            jpql.append("HAVING COUNT(DISTINCT t.id) = :tagCount ");
        } else {
            jpql.append("GROUP BY i.id "); 
        }

        if ("random".equalsIgnoreCase(sortBy)) {
            jpql.append("ORDER BY RANDOM() ");
        } else if (sortIsStatic) {
            jpql.append("ORDER BY sv_sort.value ").append("desc".equalsIgnoreCase(sortDirection) ? "DESC" : "ASC");
        } else if ("title".equalsIgnoreCase(sortBy)) {
            jpql.append("ORDER BY i.title ").append("desc".equalsIgnoreCase(sortDirection) ? "DESC" : "ASC");
        } else {
            jpql.append("ORDER BY i.id DESC"); 
        }

        jakarta.persistence.TypedQuery<Image> query = entityManager.createQuery(jpql.toString(), Image.class);

        if (title != null && !title.isBlank()) {
            query.setParameter("title", "%" + title.trim() + "%");
        }

        if (tags != null && !tags.isEmpty()) {
            query.setParameter("tagIds", tags.stream().map(Tag::getId).collect(Collectors.toSet()));
        }

        if (exactMode) {
            query.setParameter("tagCount", (long) tags.size());
        }

        if (sortIsStatic) {
            query.setParameter("sortDefId", Integer.parseInt(sortBy.substring(7)));
        }

        if (staticTagFilters != null && !staticTagFilters.isEmpty()) {
            int filterIdx = 0;
            for (Integer defId : staticTagFilters.keySet()) {
                query.setParameter("defId_" + filterIdx, defId);
                dev.asedem.mediacloud.model.RangeFilterDTO filter = staticTagFilters.get(defId);
                if (filter.min() != null) {
                    query.setParameter("min_" + filterIdx, filter.min());
                }
                if (filter.max() != null) {
                    query.setParameter("max_" + filterIdx, filter.max());
                }
                filterIdx++;
            }
        }

        return query.setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
    }

    public byte[] getImageData(Integer id) throws Exception {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return this.loadDecryptedFile(UPLOAD_DIR + image.getName() + ".enc");
    }

    public byte[] getThumbnailData(Integer id) throws Exception {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        return this.loadDecryptedFile(THUMB_DIR + image.getName() + ".enc");
    }

    @Transactional
    public Image updateImage(Integer id, String title, List<Integer> tagIds, java.util.Map<Integer, Double> staticTagValues) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        if (title != null) {
            image.setTitle(title);
        }

        if (tagIds != null) {
            image.getTags().clear();
            tagIds.stream()
                    .map(this.tagService::getTagById)
                    .forEach(image::addTag);
        }

        if (staticTagValues != null) {
            this.imageStaticTagValueRepository.deleteAllByImageId(id);
            staticTagValues.forEach((defId, value) -> {
                dev.asedem.mediacloud.database.entity.StaticTagDefinition definition = this.staticTagService.getDefinition(defId);
                this.imageStaticTagValueRepository.save(new dev.asedem.mediacloud.database.entity.ImageStaticTagValue(image, definition, value));
            });
        }

        return this.imageRepository.save(image);
    }

    public void deleteImage(Integer id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        try {
            Files.deleteIfExists(Paths.get(UPLOAD_DIR + image.getName() + ".enc"));
            Files.deleteIfExists(Paths.get(THUMB_DIR + image.getName() + ".enc"));
            this.imageRepository.delete(image);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete physical files", e);
        }
    }

    @Transactional
    public Image addTags(Integer id, List<Integer> tagIds) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        tagIds.stream()
                .map(this.tagService::getTagById)
                .forEach(image::addTag);
        return this.imageRepository.save(image);
    }

    private void saveEncryptedFile(byte[] data, String directory, String uuid) throws Exception {
        String fullPath = directory + uuid + ".enc";
        SecretKeySpec secretKey = new SecretKeySpec(KEY.substring(0, 16).getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(data);
        Files.write(Paths.get(fullPath), encryptedBytes);
    }

    private void saveEncryptedThumbnail(byte[] originalData, String uuid) throws Exception {
        ImmutableImage img = ImmutableImage.loader().fromBytes(originalData);
        byte[] thumbBytes = img.max(350, 350)
                .bytes(com.sksamuel.scrimage.nio.JpegWriter.Default);
        saveEncryptedFile(thumbBytes, THUMB_DIR, uuid);
    }

    private byte[] loadDecryptedFile(String path) throws Exception {
        byte[] encryptedBytes = Files.readAllBytes(Paths.get(path));

        SecretKeySpec secretKey = new SecretKeySpec(KEY.substring(0, 16).getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(encryptedBytes);
    }
}