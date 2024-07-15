package faang.school.projectservice.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.dto.DtoValidationConstraints;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProjectDto {

    private Long id;
    @NotBlank(message = DtoValidationConstraints.PROJECT_NAME_REQUIRED)
    @Pattern(regexp = DtoValidationConstraints.PROJECT_NAME_PATTERN, message = DtoValidationConstraints.PROJECT_NAME_CONSTRAINT)
    private String name;
    @NotBlank(message = DtoValidationConstraints.PROJECT_DESCRIPTION_REQUIRED)
    @Pattern(regexp = DtoValidationConstraints.PROJECT_DESCRIPTION_PATTERN, message = DtoValidationConstraints.PROJECT_DESCRIPTION_CONSTRAINT)
    private String description;
    @Min(value = 0, message = DtoValidationConstraints.PROJECT_STORAGE_SIZE_LOWER_LIMIT)
    @Max(value = 1024, message = DtoValidationConstraints.PROJECT_STORAGE_SIZE_UPPER_LIMIT)
    private BigInteger storageSize;
    @Min(value = 0, message = DtoValidationConstraints.PROJECT_MAX_STORAGE_SIZE_LOWER_LIMIT)
    @Max(value = 1024, message = DtoValidationConstraints.PROJECT_MAX_STORAGE_SIZE_UPPER_LIMIT)
    private BigInteger maxStorageSize;
    private Long ownerId;
    private Long parentProjectId;
    private List<Long> childrenProjectIds;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private String coverImageId;

}