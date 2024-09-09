package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper mapper;
    private final StageServiceValidator validator;
    private final List<StageFilter> filters;

    public void create(StageDto stageDto) {
        validator.validateStageDto(stageDto);
        stageRepository.save(mapper.toStage(stageDto));
    }

    public List<StageDto> getAllStages(Long projectId) {
        validator.validateProject(projectId);
        return mapper.toStageDtoList(projectRepository.getProjectById(projectId).getStages());
    }

    public StageDto getStageById(Long id) {
        return mapper.toDto(stageRepository.getById(id));
    }

    public void deleteStage(StageDto stageDto) {
        validator.validateStageDto(stageDto);
        stageRepository.deleteById(stageDto.getStageId());
    }

    public List<StageDto> getFilteredStages(Long projectId, StageFilterDto filterDto) {
        validator.validateProject(projectId);

        Project project = projectRepository.getProjectById(projectId);
        Stream<Stage> stageStream = project.getStages().stream();
        return filters.stream()
                .filter(stageFilter -> stageFilter.isApplicable(filterDto))
                .flatMap(stageFilter -> stageFilter.apply(stageStream, filterDto))
                .map(mapper::toDto)
                .toList();
    }

    public void updateStage(StageDto stageDto) {
        validator.validateStageDto(stageDto);
        sentInvitationToNeededExecutors(getNumberOfRolesInvolved(stageDto),
                stageDto);
    }

    private Map<TeamRole, Long> getNumberOfRolesInvolved(StageDto stageDto) {
        return stageDto.getExecutorsDtos().stream()
                .flatMap(teamMemberDto -> teamMemberDto.stageRoles().stream())
                .collect(Collectors.groupingBy(
                        role -> role,
                        Collectors.counting()
                ));
    }

    private void sentInvitationToNeededExecutors
            (Map<TeamRole, Long> mapRolesInvolved, StageDto stageDto) {
        for (StageRolesDto dto : stageDto.getStageRoles()) {
            if (dto.count() > mapRolesInvolved.get(dto.role())) {
                send(dto.count() - mapRolesInvolved.get(dto.role()));
            }
        }
    }

    private void send(Long countOfExecutors) {
        validator.validateCount(countOfExecutors);
        //отправка
    }
}
