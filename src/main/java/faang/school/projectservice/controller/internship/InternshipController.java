package faang.school.projectservice.controller.internship;

import faang.school.projectservice.controller.Marker;
import faang.school.projectservice.dto.filter.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internships")
@Validated
public class InternshipController {

    private final InternshipService internshipService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public InternshipDto createInternship(@Valid @RequestBody InternshipDto internshipDto) {
        return internshipService.create(internshipDto);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public InternshipDto updateInternship(@Valid @RequestBody InternshipDto internshipDto) {
        return internshipService.update(internshipDto);
    }

    @GetMapping("/filters")
    public List<InternshipDto> filterInternship(@Valid @RequestBody InternshipFilterDto filters) {
        return internshipService.getFilteredInternship(filters);
    }

    @GetMapping
    public List<InternshipDto> getInternships() {
        return internshipService.getAllInternship();
    }

    @GetMapping("/{id}")
    public InternshipDto getInternship(@PathVariable @Positive Long id) {
        return internshipService.getInternshipById(id);
    }
}
