package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = StageRolesMapper.class)
public interface StageMapper {

    @Mapping(source = "stageRoles", target = "stageRolesDto")
    StageDto toStageDto(Stage stage);

    @Mapping(source = "stageRolesDto", target = "stageRoles")
    Stage toStage(StageDto stageDto);
}
