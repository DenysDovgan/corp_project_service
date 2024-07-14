package faang.school.projectservice.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private List<StageRolesDto> stageRolesDtosList;
}
