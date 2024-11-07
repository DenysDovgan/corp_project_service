package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDtoWithRolesToFill;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.role.StageRolesMapper;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {ProjectMapper.class,
                StageRolesMapper.class,
                TaskMapper.class,
                ProjectMapper.class,},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapperWithRolesToFill {
    @Mapping(target = "rolesToBeFilled", expression = "java(calculateRolesToBeFilled(stage))")
    StageDtoWithRolesToFill toDto(Stage stage);

    @Mapping(target = "rolesToBeFilled", expression = "java(calculateRolesToBeFilled(stage))")
    List<StageDtoWithRolesToFill> toDto(List<Stage> stages);

    default List<StageRolesDto> calculateRolesToBeFilled(Stage stage) {
        Map<TeamRole, Integer> roleCountMap = stage.getStageRoles().stream()
                .collect(Collectors.toMap(
                        StageRoles::getTeamRole,
                        StageRoles::getCount
                ));

        stage.getExecutors().stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .forEach(memberRole -> roleCountMap
                        .computeIfPresent(memberRole, (role, count) -> count > 0 ? count - 1 : count));

        return roleCountMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new StageRolesDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}