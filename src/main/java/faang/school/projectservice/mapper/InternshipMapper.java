package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.internship.InternshipCreationDto;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(source = "creatorUserId", target = "createdBy")
    Internship toEntity(InternshipCreationDto creationDto);

    @Mapping(source = "mentorId.userId", target = "mentorUserId")
    @Mapping(source = "createdBy", target = "creatorUserId")
    @Mapping(source = "interns", target = "interns", qualifiedByName = "internsToIds")
    InternshipDto toDto(Internship internship);

    List<InternshipDto> toDto(List<Internship> internships);

    @Named("internsToIds")
    default List<Long> internsToIds(List<TeamMember> interns) {
        return interns.stream()
                .map(TeamMember::getUserId)
                .toList();
    }
}
