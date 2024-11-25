package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.dto.task.TaskUpdateRequestDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.task.task_filter.TaskFilter;
import faang.school.projectservice.validator.task.TaskValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;
    private final TaskValidator taskValidator;
    private final List<TaskFilter> taskFilters;

    public TaskResponseDto createTask(TaskRequestDto taskRequestDto, Long authorId) {
        taskValidator.validateUser(authorId);
        Long projectId = taskRequestDto.getProjectId();
        Project project = projectRepository.getProjectById(projectId);
        taskValidator.validateAuthorInThisProject(project, authorId);
        Task task = taskMapper.toEntity(taskRequestDto);
        task.setProject(project);
        taskRepository.save(task);
        log.info("Created task: {}", task.getId());
        return taskMapper.toDto(task);
    }

    public TaskResponseDto updateTask(TaskUpdateRequestDto updateDto, Long authorId) {
        log.info("Updating task: {} by userID {}", updateDto.getId(), authorId);
        taskValidator.validateUser(authorId);
        Task oldTask = taskValidator.validateTask(updateDto.getId());
        Project project = projectRepository.getProjectById(oldTask.getProject().getId());
        taskValidator.validateAuthorInThisProject(project, authorId);
        taskValidator.validateTaskWithStatusCancelled(oldTask);
        Task newTask = toNewTask(updateDto, oldTask);
        taskRepository.save(newTask);
        log.info("Updated task: {}", newTask.getId());
        return taskMapper.toDto(newTask);
    }

    public List<TaskResponseDto> getTasksByFilters(TaskFilterDto filterDto, Long projectId, Long authorId) {
        taskValidator.validateUser(authorId);
        Project project = projectRepository.getProjectById(projectId);
        taskValidator.validateAuthorInThisProject(project, authorId);
        Stream<Task> tasks = taskRepository.findAllByProjectId(projectId).stream();
        taskFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(tasks, filterDto));
        log.info("A request to receive all project {} tasks" +
                " by filter from a user {} has been processed ", projectId, authorId);
        return taskMapper.toDto(tasks.toList());
    }

    public List<TaskResponseDto> getTasks(Long projectId, Long authorId) {
        log.info("Getting all tasks by userID: {}", authorId);
        taskValidator.validateUser(authorId);
        Project project = projectRepository.getProjectById(projectId);
        taskValidator.validateAuthorInThisProject(project, authorId);
        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        log.info("The request to receive all tasks from the user {} was completed successfully", authorId);
        return taskMapper.toDto(tasks);
    }

    public TaskResponseDto getTask(Long taskId, Long authorId) {
        log.info("Getting task by userID: {}", authorId);
        taskValidator.validateUser(authorId);
        Task task = taskValidator.validateTask(taskId);
        Project project = projectRepository.getProjectById(task.getProject().getId());
        taskValidator.validateAuthorInThisProject(project, authorId);
        log.info("The request to receive task from the user {} was completed successfully", authorId);
        return taskMapper.toDto(task);
    }

    private Task toNewTask(TaskUpdateRequestDto updateDto, Task oldTask) {
        if (updateDto.getName() != null) {
            oldTask.setName(updateDto.getName());
        }
        if (updateDto.getDescription() != null) {
            oldTask.setDescription(updateDto.getDescription());
        }
        if (updateDto.getStatus() != null) {
            oldTask.setStatus(updateDto.getStatus());
        }
        if (updateDto.getMinutesTracked() != null) {
            oldTask.setMinutesTracked(updateDto.getMinutesTracked());
        }
        if (updateDto.getPerformerUserId() != null) {
            long userId = updateDto.getPerformerUserId();
            taskValidator.validateUser(userId);
            oldTask.setPerformerUserId(userId);
        }
        if (updateDto.getReporterUserId() != null) {
            long userId = updateDto.getReporterUserId();
            taskValidator.validateUser(userId);
            oldTask.setReporterUserId(userId);
        }
        if (updateDto.getLinkedTasksIds() != null) {
            List<Task> linkedTasks = updateDto.getLinkedTasksIds()
                    .stream().map(taskValidator::validateTask)
                    .toList();
            oldTask.setLinkedTasks(linkedTasks);
        }
        if (updateDto.getParentTaskId() != null) {
            Task task = taskValidator.validateTask(updateDto.getParentTaskId());
            oldTask.setParentTask(task);
        }
        if (updateDto.getStageId() != null) {
            Stage stage = stageRepository.getById(updateDto.getStageId());
            oldTask.setStage(stage);
        }
        return oldTask;
    }

}
