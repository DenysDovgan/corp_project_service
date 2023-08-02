package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageDtoForUpdate;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final StageValidator stageValidator;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        log.debug("StageDto mapped to entity is successfully");
        stage.getStageRoles().forEach(stageRole ->
                stageRole.setStage(stage)
        );
        log.debug("Sat stage on field stage in StageRoles");
        return stageMapper.toDto(stageRepository.save(stage));
    }

    @Transactional(readOnly = true)
    public List<StageDto> getAllStagesByStatus(String status) {
        List<Stage> stages = stageRepository.findAll();
        stages.removeIf(stage -> !stage.getStatus().toString().equalsIgnoreCase(status));
        return stages.stream().map(stageMapper::toDto).toList();
    }

    @Transactional
    public void deleteStageById(Long stageId) {
        stageRepository.deleteById(stageId);
    }

    @Transactional(readOnly = true)
    public List<StageDto> getAllStages() {
        List<Stage> stages = stageRepository.findAll();
        return stages.stream().map(stageMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public StageDto getStageById(Long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }

    @Transactional
    public StageDto updateStage(StageDtoForUpdate stageDto) {

        Stage stageFromRepository = stageRepository.getById(stageDto.getStageId());

        stageValidator.isCompletedOrCancelled(stageFromRepository);
        TeamMember author = teamMemberRepository.findById(stageDto.getAuthorId());
        List<TeamRole> teamRolesFromStageDto = stageDto.getTeamRoles();

        List<TeamRole> newTeamRoles = findNewTeamRoles(stageFromRepository, teamRolesFromStageDto);
        Stage stageAfterUpdate = setNewFieldsForStage(stageFromRepository, stageDto);
        sendStageInvitation(stageFromRepository, newTeamRoles, author);

        return stageMapper.toDto(stageRepository.save(stageAfterUpdate));
    }

    private Stage setNewFieldsForStage(Stage stageFromRepository, StageDtoForUpdate stageDto) {
        Map<String, Long> teamRolesMap = stageDto.getTeamRoles().stream().collect(Collectors.groupingBy(TeamRole::toString, Collectors.counting()));
        List<StageRoles> newStageRoles = new ArrayList<>();

        for (Map.Entry<String, Long> entry : teamRolesMap.entrySet()) {
            Integer count = Math.toIntExact(entry.getValue());
            newStageRoles.add(StageRoles.builder().teamRole(TeamRole.valueOf(entry.getKey())).count(count).build());
        }
        stageFromRepository.setStageRoles(newStageRoles);
        stageFromRepository.setStageName(stageDto.getStageName());
        stageFromRepository.setStatus(stageDto.getStatus());

        return stageFromRepository;
    }

    private void sendStageInvitation(Stage stageFromRepository, List<TeamRole> newTemRoles, TeamMember author) {
        if (!newTemRoles.isEmpty()) {
            Project projectFromRepository = projectRepository.getProjectById(stageFromRepository.getProject().getId());

            newTemRoles
                    .forEach(teamRole ->
                            projectFromRepository.getTeams()
                                    .stream()
                                    .flatMap(team -> team.getTeamMembers()
                                            .stream())
                                    .filter(teamMember -> teamMember.getRoles().contains(teamRole))
                                    .forEach(teamMember -> StageInvitation.builder()
                                            .invited(teamMember)
                                            .author(author)
                                            .stage(stageFromRepository)
                                            .status(StageInvitationStatus.PENDING)
                                            .build()));
        }
    }

    private List<TeamRole> findNewTeamRoles(Stage stageFromRepository, List<TeamRole> teamRoles) {

        stageFromRepository.getExecutors()
                .forEach(teamMember -> teamMember.getRoles()
                        .forEach(teamRole -> teamRoles
                                .removeIf(role -> role.equals(teamRole))));

        return teamRoles;
    }
}