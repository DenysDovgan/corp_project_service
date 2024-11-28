package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.utils.image.ResizeOptions;
import faang.school.projectservice.validator.FileValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final FileValidator fileValidator;
    private final S3Service s3Service;

    @Transactional
    public void addCover(long projectId, MultipartFile file) {
        log.info("Trying to add cover to project: {}", projectId);
        long maxAllowedSize = 5 * 1024 * 1024;
        fileValidator.validateFileSize(file, maxAllowedSize);
        fileValidator.validateFileIsImage(file);

        boolean requiresResizing = true;
        int maxWidth = 1080;
        int maxHeight = 566;
        ResizeOptions resizeOptions = new ResizeOptions(requiresResizing, maxWidth, maxHeight);

        String folder = "projectCovers";
        String coverImageId = s3Service.uploadFile(file, folder, resizeOptions);

        Project project = findProjectById(projectId);
        String oldCoverImageId = project.getCoverImageId();
        if (oldCoverImageId != null) {
            s3Service.deleteFile(oldCoverImageId);
        }

        project.setCoverImageId(coverImageId);
    }

    public Project findProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }
}
