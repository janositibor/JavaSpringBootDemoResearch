package research.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import research.criteria.ProjectCriteria;
import research.dtos.*;
import research.service.ProjectsAndGroupsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectsAndGroupsService projectsAndGroupsService;

    public ProjectController(ProjectsAndGroupsService projectsAndGroupsService) {
        this.projectsAndGroupsService = projectsAndGroupsService;
    }

    @Operation(summary = "Create project")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project has been successfully created"),
            @ApiResponse(responseCode = "400", description = "Problem with input data"),
            @ApiResponse(responseCode = "409", description = "The project already exists")
    })
    @Tag(name="11. Create project")
    public ProjectDto createProject(@Valid @RequestBody CreateProjectCommand createCarCommand){
        return projectsAndGroupsService.createProject(createCarCommand);
    }

    @Operation(summary = "Get projects")
    @GetMapping
    @Tag(name="12. Read all or filtered projects")
    public List<ProjectDto> getProjects(ProjectCriteria projectCriteria){
        return  projectsAndGroupsService.getProjects(projectCriteria);
    }

    @Operation(summary = "Get project by id")
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project has been successfully found"),
            @ApiResponse(responseCode = "404", description = "No project found with this ID")
    })
    @Tag(name="13. Read project  by id")
    public ProjectDto getProjectById( @PathVariable("id") long projectId){
        return projectsAndGroupsService.getProjectById(projectId);
    }

    @Operation(summary = "Update project by id")
    @PutMapping("/update/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project has been successfully updated"),
            @ApiResponse(responseCode = "400", description = "Problem with input date"),
            @ApiResponse(responseCode = "404", description = "No project found with this ID")
    })
    @Tag(name="14. Update project")
    public ProjectDto updateProject(@PathVariable("id") @Parameter(name = "id", description = "Project ID to update", example = "2") long id, @RequestBody UpdateProjectCommand updateProjectCommand){
        return projectsAndGroupsService.updateProject(id, updateProjectCommand);
    }

    @Operation(summary = "Add a new group to a project")
    @PostMapping("/{id}/add-group")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Research group has been successfully added to project"),
            @ApiResponse(responseCode = "400", description = "Problem with input data"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "409", description = "The research group already exists")
    })
    @Tag(name="15. Post research group to project")
    public ProjectDto addPostedGroupToProject(@PathVariable("id") long id, @Valid @RequestBody CreateResearchGroupCommand createResearchGroupCommand){
        return projectsAndGroupsService.addPostedGroupToProject(id,createResearchGroupCommand);
    }

    @Operation(summary = "Add an existing group to a project")
    @GetMapping("/{id}/add-group")
    @ApiResponse(responseCode = "200", description = "Research group has been added")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Research group has been successfully added to project"),
            @ApiResponse(responseCode = "404", description = "Project or research group not found")
    })
    @Tag(name="16. Link research group to project")
    public ProjectDto addGroupToProject(@PathVariable("id") long projectId, @RequestParam  @Parameter(name = "groupId", description = "ID of the group", example = "1") long groupId){
        return projectsAndGroupsService.addGroupToProject(projectId,groupId);
    }

    @Operation(summary = "Delete a group from a project")
    @GetMapping("/{id}/delete-group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Research group has been successfully deleted from project"),
            @ApiResponse(responseCode = "404", description = "Project or research group not found")
    })
    @Tag(name="17. Unlink research group from project")
    public ProjectDto deleteGroupFromProject(@PathVariable("id") long projectId, @RequestParam  @Parameter(name = "groupId", description = "ID of the group", example = "1") long groupId){
        return projectsAndGroupsService.deleteGroupFromProject(projectId,groupId);
    }

    @Operation(summary = "Delete project")
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project has been successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @Tag(name="18. Delete project")
    public void deleteProject(@PathVariable("id") @Parameter(name = "id", description = "Project ID to delete", example = "2") long id){
        projectsAndGroupsService.deleteProject(id);
    }
}
