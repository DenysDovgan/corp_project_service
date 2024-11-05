package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectResource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Map;

@Slf4j
@Component
public class ProjectResourceManager {
    private final ProjectResourceService projectResourceService;
    private final AmazonS3 s3Client;
    private final String projectBucket;
    private final String DIRECTORY_NAME;
    private final long MAX_STORAGE_SIZE;
    private final long MAX_COVER_FILE_SIZE;
    private final long COVER_MAX_RECTANGLE_WIDTH;
    private final long COVER_MAX_RECTANGLE_HEIGHT;
    private final long COVER_MAX_SQUARE_DIMENSION;

    public ProjectResourceManager(AmazonS3 s3Client,
                                  ProjectResourceService projectResourceService,
                                  @Value("${services.s3.bucketName}") String projectBucket,
                                  @Value("${project-service.directory}") String DIRECTORY_NAME,
                                  @Value("${project-service.max_storage_size_no_subscription}") long MAX_STORAGE_SIZE,
                                  @Value("${services.cover_image.max_cover_file_size}") long MAX_COVER_FILE_SIZE,
                                  @Value("${services.cover_image.max-rectangle-width}") long COVER_MAX_RECTANGLE_WIDTH,
                                  @Value("${services.cover_image.max-rectangle-height}") long COVER_MAX_RECTANGLE_HEIGHT,
                                  @Value("${services.cover_image.max-square-dimension}") long COVER_MAX_SQUARE_DIMENSION) {
        this.s3Client = s3Client;
        this.projectResourceService = projectResourceService;
        this.projectBucket = projectBucket;
        this.MAX_STORAGE_SIZE = MAX_STORAGE_SIZE;
        this.DIRECTORY_NAME = DIRECTORY_NAME;
        this.MAX_COVER_FILE_SIZE = MAX_COVER_FILE_SIZE;
        this.COVER_MAX_RECTANGLE_WIDTH = COVER_MAX_RECTANGLE_WIDTH;
        this.COVER_MAX_RECTANGLE_HEIGHT = COVER_MAX_RECTANGLE_HEIGHT;
        this.COVER_MAX_SQUARE_DIMENSION = COVER_MAX_SQUARE_DIMENSION;
    }

    @Async
    public void uploadFileS3Async(MultipartFile file, ProjectResource projectResource, ObjectMetadata metadata) {
        log.info("Uploading file: {} to bucket: {}", file.getOriginalFilename(), projectBucket);
        try {
            PutObjectResult result = s3Client.putObject(projectBucket, projectResource.getKey(), file.getInputStream(), metadata);
            String eTag = result.getETag();
            log.info("File uploaded successfully. ETag: {}", eTag);
            projectResourceService.setStatus(projectResource, ResourceStatus.ACTIVE);
        } catch (Exception e) {
            projectResourceService.setStatus(projectResource, ResourceStatus.FAILED);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Async
    public void deleteFileS3Async(ProjectResource projectResource) {
        s3Client.deleteObject(projectBucket, projectResource.getKey());
        projectResourceService.setStatus(projectResource, ResourceStatus.DELETED);
    }

    @Async
    public void deleteFileS3Async(String key) {
        s3Client.deleteObject(projectBucket, key);
    }

    public Resource getFileS3ByKey(String objectKey) {
        try (InputStream inputStream = s3Client.getObject(projectBucket, objectKey).getObjectContent()) {
            return new ByteArrayResource(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to get file from S3", e);
        }
    }

    public Pair<ProjectResource, ObjectMetadata> getProjectCoverBeforeUploadFile(MultipartFile file, Project project, TeamMember teamMember) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cover's file not be empty");
        }
        if (file.getSize() > MAX_COVER_FILE_SIZE) {
            throw new IllegalStateException("Cover's file size exceeds project limit");
        }
        String fileNameWithExtension = generateUniqueFileName(file.getOriginalFilename());
        String path = generateFullPathForUserFiles(file, project.getId(), teamMember.getId(), fileNameWithExtension);
        ObjectMetadata metadata = generateMetadata(file, path, fileNameWithExtension);
        if (s3Client.doesObjectExist(projectBucket, path)) {
            throw new IllegalStateException("File already exists");
        }
        ProjectResource projectResource = createProjectResourceByMetadata(metadata, project, teamMember);
        return Pair.of(projectResource, metadata);
    }

    public Pair<ProjectResource, ObjectMetadata> getProjectResourceBeforeUploadFile(MultipartFile file, Project project, TeamMember teamMember) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File not be empty");
        }
        if (project.getStorageSize().longValue() + file.getSize() > MAX_STORAGE_SIZE) {
            throw new IllegalStateException("File size exceeds project limit");
        }
        String fileNameWithExtension = generateUniqueFileName(file.getOriginalFilename());
        String path = generateFullPathForUserFiles(file, project.getId(), teamMember.getId(), fileNameWithExtension);
        ObjectMetadata metadata = generateMetadata(file, path, fileNameWithExtension);
        if (s3Client.doesObjectExist(projectBucket, path)) {
            throw new IllegalStateException("File already exists");
        }
        ProjectResource projectResource = createProjectResourceByMetadata(metadata, project, teamMember);
        return Pair.of(projectResource, metadata);
    }

    public ProjectResource createProjectResourceByMetadata(ObjectMetadata metadata, Project project, TeamMember teamMember) {
        String path = metadata.getUserMetaDataOf("path");
        String fileName = metadata.getUserMetaDataOf("name");
        String type = metadata.getContentType();
        BigInteger size = BigInteger.valueOf(metadata.getContentLength());
        return projectResourceService.getProjectResource(project, teamMember, path, fileName, size, type);
    }

    private ObjectMetadata generateMetadata(MultipartFile file, String path, String fileNameWithExtension) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        metadata.setUserMetadata(Map.of(
                "path", path,
                "name", fileNameWithExtension));
        return metadata;
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if (originalFilename == null || originalFilename.isEmpty()) {
            return String.valueOf(System.currentTimeMillis());
        }
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String baseName = originalFilename.substring(0, dotIndex > 0 ? dotIndex : originalFilename.length());
        return baseName + "_" + System.currentTimeMillis() + extension;
    }

    private String generateFullPathForUserFiles(MultipartFile file, Long entityId,
                                                Long teamMemberId, String fileNameWithExtension) {
        String directoryPath = generateDirectoriesForUserFiles(entityId, teamMemberId);
        return String.format("%s/%s/%s", directoryPath, file.getContentType(), fileNameWithExtension);
    }

    private String generateDirectoriesForUserFiles(Long entityId, Long teamMemberId) {
        return String.format("%s/%d/%d", DIRECTORY_NAME, entityId, teamMemberId);
    }


}