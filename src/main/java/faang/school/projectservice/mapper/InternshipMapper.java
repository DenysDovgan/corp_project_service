package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.TeamRoleDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(source = "mentorId", target = "mentorId")
    @Mapping(source = "project.id", target = "projectId")
    InternshipDto internshipToInternshipDto(Internship internship);

    @Mapping(source = "mentorId", target = "mentorId")
    Internship createInternshipDtoToInternship(CreateInternshipDto internshipDto);

    @Mapping(source = "mentorId", target = "mentorId")
    @Mapping(source = "projectId", target = "project.id")
    Internship internshipDtoToInternship(InternshipDto internshipDto);

    List<Internship> internshipDtosToInternships(List<InternshipDto> internshipDtos);

    List<InternshipDto> internshipsToInternshipDtos(List<Internship> internships);

    default TeamRole teamRoleDtoToStringTeamRole(TeamRoleDto teamRoleDto) {
        return TeamRole.valueOf(teamRoleDto.role());
    }

    default Long teamMemberToLong(TeamMember teamMember) {
        return teamMember != null ? teamMember.getId() : null;
    }

    default TeamMember longToTeamMember(Long mentorId) {
        if (mentorId == null) {
            return null;
        }
        TeamMember teamMember = new TeamMember();
        teamMember.setId(mentorId);
        return teamMember;
    }
}