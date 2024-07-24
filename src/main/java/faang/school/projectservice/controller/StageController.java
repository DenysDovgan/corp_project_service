package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.FateOfTasksAfterDelete;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
public class StageController {

    private final StageService stageService;

    @PostMapping("/stage")
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @PostMapping("/filter_stage")
    public List<StageDto> getFilteredStages(@RequestBody StageFilterDto filterDto) {
        return stageService.getFilteredStages(filterDto);
    }

    @DeleteMapping("/stage/{deletedStageId}")
    public void deleteStage(@PathVariable Long deletedStageId,
                            @RequestParam FateOfTasksAfterDelete tasksAfterDelete,
                            @RequestParam Long receivingStageId) {

        stageService.deleteStage(deletedStageId, tasksAfterDelete, receivingStageId);
    }

    @PutMapping("/stage")
    public StageDto updateStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.updateStage(stageDto);
    }

    @GetMapping("/stages")
    public List<StageDto> getAllStages() {
        return stageService.getAllStages();
    }

    @GetMapping("/stage/{id}")
    public StageDto getStage(@Positive @PathVariable Long id) {
        return stageService.getStage(id);
    }
}
