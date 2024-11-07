package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubProjectDto {

    @NotNull
    private Long ownerId;

    @NotBlank
    @Size(max = 128)
    private String name;

    @NotBlank
    @Size(max = 4096)
    private String description;

    @NotNull
    private ProjectVisibility visibility;
}
