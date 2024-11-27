package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moment")
public class MomentController {
    private final MomentService momentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MomentDto createMoment(@Valid @RequestBody MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MomentDto updateMoment(@PathVariable Long id, @Valid @RequestBody MomentDto momentDto) {
        return momentService.updateMoment(id, momentDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMoment(@PathVariable Long id) {
        momentService.deleteMoment(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MomentDto getMomentById(@PathVariable Long id) {
        return momentService.getMomentById(id);
    }

    @PostMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<MomentDto> filterMoments(@Valid @RequestBody MomentFilterDto momentFilterDto) {
        return momentService.filterMomentsByDate(momentFilterDto);
    }
}
