package research.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import research.criteria.ResearchGroupCriteria;
import research.dtos.*;
import research.service.ProjectsAndGroupsService;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/research-groups")
public class ResearchGroupController {
    private final ProjectsAndGroupsService projectsAndGroupsService;

    public ResearchGroupController(ProjectsAndGroupsService projectsAndGroupsService) {
        this.projectsAndGroupsService = projectsAndGroupsService;
    }

    @Operation(summary = "Create research group")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Research group has been successfully created"),
            @ApiResponse(responseCode = "400", description = "Problem with input data"),
            @ApiResponse(responseCode = "409", description = "Research group already exists")
    })
    @Tag(name="01. Create research group")
    public ResearchGroupDto createResearchGroup(@Valid @RequestBody CreateResearchGroupCommand createResearchGroupCommand){
        return projectsAndGroupsService.createResearchGroup(createResearchGroupCommand);
    }

    @Operation(summary = "Get research groups")
    @GetMapping
    @Tag(name="02. Read all or filtered research groups")
    public List<ResearchGroupDto> getResearchGroups(ResearchGroupCriteria researchGroupCriteria){
        return  projectsAndGroupsService.getResearchGroups(researchGroupCriteria);
    }

    @Operation(summary = "Get research group by id")
    @GetMapping("/{id}")
    @Tag(name="03. Read research group by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Research group has been found"),
            @ApiResponse(responseCode = "404", description = "No research group found with this ID")
    })
    public ResearchGroupDto getResearchGroupById(@PathVariable("id") long id){
        return projectsAndGroupsService.getResearchGroupById(id);
    }

    @Operation(summary = "Update research group by id")
    @PutMapping("/update/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Research group has been updated"),
            @ApiResponse(responseCode = "400", description = "Problem with input date"),
            @ApiResponse(responseCode = "404", description = "No research group found with this ID")
    })
    @Tag(name="04. Update research group")
    public ResearchGroupDto updateResearchGroupById(@PathVariable("id") @Parameter(name = "id", description = "Research group ID to update", example = "2") long id, @RequestBody UpdateResearchGroupCommand updateResearchGroupCommand){
        return projectsAndGroupsService.updateResearchGroupById(id, updateResearchGroupCommand);
    }

    @Operation(summary = "Delete research group")
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Research group has been successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Research group not found")
    })
    @Tag(name="05. Delete research group")
    public void deleteResearchGroup(@PathVariable("id") @Parameter(name = "id", description = "Research group ID to delete", example = "2") long id){
        projectsAndGroupsService.deleteResearchGroup(id);
    }


}
