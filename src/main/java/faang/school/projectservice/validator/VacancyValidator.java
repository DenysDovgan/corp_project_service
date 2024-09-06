package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.LongStream;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyRepository vacancyRepository;

    public void validateExistingOfProject(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException("The project doesn't exist in the System");
        }
    }

    public void validateVacancyCreator(long authorId, long projectId){
        long ownerId = projectRepository.getProjectById(projectId).getOwnerId();
        if (authorId != ownerId) {
            throw new DataValidationException("Only project owner can create new vacancy");
        }
    }


    private void checkCreatorIsMemberOfTeam(long projectId, long memberId) {
        Project project = projectRepository.getProjectById(projectId);
        List<Team> teamsOnProject = project.getTeams();
        List<Long> teamsMembersIds = teamsOnProject.stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .distinct()
                .toList();

        if (!teamsMembersIds.contains(memberId)) {
            throw new DataValidationException("The creator doesn't work on the project");
        }
    }

    private void validateIsProjectOwnerIsCreatingVacancy(long memberId, long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        long ownerId = project.getOwnerId();

        if (memberId != projectId) {
            throw new DataValidationException("The project Owner is different");
        }
    }

    private void validateStatusOfVacancyCreator(long memberId) {
        TeamMember teamMember = teamMemberRepository.findById(memberId);
        List<TeamRole> teamRoles = teamMember.getRoles();
        if (!(teamRoles.contains(TeamRole.OWNER))) {
            throw new DataValidationException("The role does not allow to create a vacancy");
        }
    }

    public void checkExistingOfVacancy(long vacancyId) {
        if (!vacancyRepository.existsById(vacancyId)) {
            throw new EntityNotFoundException("The vacancy doesn't exist in the system ID = " + vacancyId);
        }
    }
}
