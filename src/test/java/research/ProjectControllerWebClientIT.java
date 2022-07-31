package research;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;
import research.dtos.*;
import research.model.Location;
import research.service.ProjectsAndGroupsService;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = {"DELETE FROM project_researchgroup","DELETE FROM research_groups", "DELETE FROM projects"})
class ProjectControllerWebClientIT {
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ProjectsAndGroupsService projectsAndGroupsService;

    ResearchGroupDto researchGroupDto1;
    ResearchGroupDto researchGroupDto2;
    ProjectDto projectDto1;
    ProjectDto projectDto2;

    @BeforeEach
    void init(){
        webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("FEMTO-Lézeres Csoport",LocalDate.of(2016,6,1),7,Location.BIOFIZIKA,15))
                .exchange();
        webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("NAP kutatócsoport", LocalDate.of(2018,9,1),15, Location.SZENTÁGOTHAI_KUTATÓKÖZPONT,5))
                .exchange();
        researchGroupDto1=webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("Nano-Bio-Imaging Core Facility",LocalDate.of(2019,7,19),3,Location.ÁOK_ÚJ_ÉPÜLET,13))
                .exchange()
                .expectBody(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();
        researchGroupDto2=webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("Neuroendokrinológia",LocalDate.of(2019,1,13),15,Location.ÁOK_RÉGI_ÉPÜLET,14))
                .exchange()
                .expectBody(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();

        webTestClient
                .post()
                .uri("/api/projects")
                .bodyValue(new CreateProjectCommand("E2 gyors hatásai virusokra",LocalDate.of(2019,1,1),145))
                .exchange();
        projectDto1=webTestClient
                .post()
                .uri("/api/projects")
                .bodyValue(new CreateProjectCommand("Covid-19",LocalDate.of(2019,2,23),195))
                .exchange()
                .expectBody(ProjectDto.class)
                .returnResult()
                .getResponseBody();
        projectDto2=webTestClient
                .post()
                .uri("/api/projects")
                .bodyValue(new CreateProjectCommand("3D Single Molecule Detection (virtual)",LocalDate.of(2022,9,30),75))
                .exchange()
                .expectBody(ProjectDto.class)
                .returnResult()
                .getResponseBody();
    }
    @Test
    @DisplayName("Create a project")
    void testCreateProject(){
        ProjectDto created=webTestClient
                .post()
                .uri("/api/projects")
                .bodyValue(new CreateProjectCommand("Ampa receptorok élő neuronokon",LocalDate.of(2020,7,1),55))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProjectDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(created.getName()).isEqualTo("Ampa receptorok élő neuronokon");
        assertThat(created.getStartDate()).isEqualTo(LocalDate.of(2020,7,1));
        assertThat(created.getBudget()).isEqualTo(55);
    }

    @Test
    @DisplayName("Create a project with invalid budget")
    void testCreateResearchGroupWithInvalidBudget() {
        ConstraintViolationProblem result=webTestClient
                .post()
                .uri("/api/projects")
                .bodyValue(new CreateProjectCommand("Ampa receptorok élő neuronokon",LocalDate.of(2020,7,1),-55))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ConstraintViolationProblem.class)
                .returnResult()
                .getResponseBody();

        assertEquals(Status.BAD_REQUEST,result.getStatus());
        assertEquals("budget",result.getViolations().get(0).getField());
        assertEquals("The budget of project mustn't be negative!",result.getViolations().get(0).getMessage());
    }

    @Test
    @DisplayName("Create a project without starting date")
    void testCreateResearchGroupWithNullStartDate() {
        ConstraintViolationProblem result=webTestClient
                .post()
                .uri("/api/projects")
                .bodyValue(new CreateProjectCommand("Ampa receptorok élő neuronokon",null,55))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ConstraintViolationProblem.class)
                .returnResult()
                .getResponseBody();

        assertEquals(Status.BAD_REQUEST,result.getStatus());
        assertEquals("startDate",result.getViolations().get(0).getField());
        assertEquals("The starting date of the project must be valid!",result.getViolations().get(0).getMessage());
    }

    @Test
    @DisplayName("Create a project with invalid date")
    void testCreateResearchGroupWithInvalidDate() {
        Problem result=webTestClient
                .post()
                .uri("/api/projects")
                .bodyValue(new CreateProjectCommand("Ampa receptorok élő neuronokon",LocalDate.of(2301,7,1),55))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(Problem.class)
                .returnResult()
                .getResponseBody();

        assertThat(result.getDetail())
                .contains("Project (name:Ampa receptorok élő neuronokon, startDate:")
                .contains(", budget:55) not valid");
        assertEquals(Status.BAD_REQUEST,result.getStatus());
        assertEquals("Not Valid",result.getTitle());
        assertEquals("projects/not-valid",result.getType().getPath());
    }

    @Test
    @DisplayName("Create a project which already exists")
    void testCreateProjectAlreadyExists() {
        Problem result=webTestClient
                .post()
                .uri("/api/projects")
                .bodyValue(new CreateProjectCommand("Covid-19",LocalDate.of(2019,2,23),195))
                .exchange()
                .expectBody(Problem.class)
                .returnResult()
                .getResponseBody();

        assertEquals("Project (name:Covid-19) already exists with id: "+projectDto1.getId(),result.getDetail());
        assertEquals(Status.CONFLICT,result.getStatus());
        assertEquals("Already Exists",result.getTitle());
        assertEquals("projects/already-exists",result.getType().getPath());
    }

    @Test
    @DisplayName("Post a research group to a project")
    void testPostResearchGroupToProject() {
        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}/add-group").build(projectDto1.getId()))
                .bodyValue(new CreateResearchGroupCommand("Szövettani mikroszkópia",LocalDate.of(2020,7,21),5,Location.SZENTÁGOTHAI_KUTATÓKÖZPONT,6))
                .exchange()
                .expectStatus().isCreated();

        ProjectDto result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}").build(projectDto1.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProjectDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result.getName()).isEqualTo("Covid-19");
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2019,2,23));
        assertThat(result.getBudget()).isEqualTo(195);
        assertThat(result.getResearchGroupSet()).hasSize(1);
        assertThat(result.getResearchGroupSet())
                .extracting(ResearchGroupWithoutProjectsDto::getName)
                .containsOnly("Szövettani mikroszkópia");
    }

    @Test
    @DisplayName("Post a research group with negative count of researcher to a project")
    void testPostResearchGroupWithNegativeCountOfResearcherToProject() {
        ConstraintViolationProblem result=webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}/add-group").build(projectDto1.getId()))
                .bodyValue(new CreateResearchGroupCommand("Szövettani mikroszkópia",LocalDate.of(2020,7,21),-5,Location.SZENTÁGOTHAI_KUTATÓKÖZPONT,6))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ConstraintViolationProblem.class)
                .returnResult()
                .getResponseBody();

        assertEquals(Status.BAD_REQUEST,result.getStatus());
        assertEquals("countOfResearchers",result.getViolations().get(0).getField());
        assertEquals("The number of researchers in the research group must be positive!",result.getViolations().get(0).getMessage());


    }

    @Test
    @DisplayName("Link a research group to a project")
    void testLinkResearchGroupToProject() {
        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}/add-group").build(projectDto1.getId()))
                .bodyValue(new CreateResearchGroupCommand("Szövettani mikroszkópia",LocalDate.of(2020,7,21),5,Location.SZENTÁGOTHAI_KUTATÓKÖZPONT,6))
                .exchange()
                .expectStatus().isCreated();
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}/add-group")
                        .queryParam("groupId",researchGroupDto1.getId())
                        .build(projectDto1.getId()))

                .exchange()
                .expectStatus().isOk();

        ProjectDto result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}").build(projectDto1.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProjectDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result.getName()).isEqualTo("Covid-19");
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2019,2,23));
        assertThat(result.getBudget()).isEqualTo(195);
        assertThat(result.getResearchGroupSet()).hasSize(2);
        assertThat(result.getResearchGroupSet())
                .extracting(ResearchGroupWithoutProjectsDto::getName)
                .containsOnly("Szövettani mikroszkópia","Nano-Bio-Imaging Core Facility");
    }

    @Test
    @DisplayName("Check group side after linking a research group to a project")
    void testGroupLinkResearchGroupToProject() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}/add-group")
                        .queryParam("groupId",researchGroupDto1.getId())
                        .build(projectDto1.getId()))
                .exchange()
                .expectStatus().isOk();
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}/add-group")
                        .queryParam("groupId",researchGroupDto1.getId())
                        .build(projectDto2.getId()))
                .exchange()
                .expectStatus().isOk();

        ResearchGroupDto result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/research-groups/{id}").build(researchGroupDto1.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result.getName()).isEqualTo("Nano-Bio-Imaging Core Facility");
        assertThat(result.getFounded()).isEqualTo(LocalDate.of(2019,7,19));
        assertThat(result.getCountOfResearchers()).isEqualTo(3);
        assertThat(result.getLocation()).isEqualTo(Location.ÁOK_ÚJ_ÉPÜLET);
        assertThat(result.getBudget()).isEqualTo(13);
        assertThat(result.getProjectSet()).hasSize(2);
        assertThat(result.getProjectSet())
                .extracting(ProjectWithoutGroupsDto::getName)
                .containsOnly("Covid-19","3D Single Molecule Detection (virtual)");
    }

    @Test
    @DisplayName("Unlink a research group from a project")
    void testUnlinkResearchGroupFromProject() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}/add-group")
                        .queryParam("groupId",researchGroupDto1.getId())
                        .build(projectDto1.getId()))
                .exchange()
                .expectStatus().isOk();
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}/add-group")
                        .queryParam("groupId",researchGroupDto2.getId())
                        .build(projectDto1.getId()))
                .exchange()
                .expectStatus().isOk();

        ProjectDto result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}").build(projectDto1.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProjectDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result.getName()).isEqualTo("Covid-19");
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2019,2,23));
        assertThat(result.getBudget()).isEqualTo(195);
        assertThat(result.getResearchGroupSet()).hasSize(2);
        assertThat(result.getResearchGroupSet())
                .extracting(ResearchGroupWithoutProjectsDto::getFounded)
                .containsOnly(LocalDate.of(2019,7,19),LocalDate.of(2019,1,13));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}/delete-group")
                        .queryParam("groupId",researchGroupDto1.getId())
                        .build(projectDto1.getId()))
                .exchange()
                .expectStatus().isOk();

        result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}").build(projectDto1.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProjectDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result.getName()).isEqualTo("Covid-19");
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2019,2,23));
        assertThat(result.getBudget()).isEqualTo(195);
        assertThat(result.getResearchGroupSet()).hasSize(1);
        assertThat(result.getResearchGroupSet())
                .extracting(ResearchGroupWithoutProjectsDto::getFounded)
                .containsOnly(LocalDate.of(2019,1,13));
    }

    @Test
    @DisplayName("Read all projects")
    void testGetAllResearchGroups(){
        List<ProjectDto> result=webTestClient
                .get()
                .uri("/api/projects")
                .exchange()
                .expectBodyList(ProjectDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result)
                .hasSize(3)
                .contains(new ProjectDto(0L,"E2 gyors hatásai virusokra",LocalDate.of(2019,1,1),145, new HashSet<>()))
                .extracting(ProjectDto::getName)
                .containsExactly("E2 gyors hatásai virusokra","Covid-19","3D Single Molecule Detection (virtual)");
    }

    @Test
    @DisplayName("Read filtered and sorted projects (1)")
    void testFilteredProjectsV1(){
        List<ProjectDto> result=webTestClient
                .get()
                .uri(builder -> builder.path("/api/projects")
                        .queryParam("startBefore","2022-07-30")
                        .queryParam("minBudget","100")
                        .queryParam("orderBy","startDate")
                        .queryParam("OrderType","desc")
                        .build())
                .exchange()
                .expectBodyList(ProjectDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result)
                .hasSize(2)
                .extracting(ProjectDto::getName)
                .containsExactly("Covid-19","E2 gyors hatásai virusokra");
    }

    @Test
    @DisplayName("Read filtered and sorted projects (2)")
    void testFilteredProjectsV2(){
        List<ProjectDto> result=webTestClient
                .get()
                .uri(builder -> builder.path("/api/projects")
                        .queryParam("nameLike","vi")
                        .queryParam("orderBy","id")
                        .queryParam("OrderType","desc")
                        .build())
                .exchange()
                .expectBodyList(ProjectDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result)
                .hasSize(3)
                .extracting(ProjectDto::getName)
                .containsExactly("3D Single Molecule Detection (virtual)","Covid-19","E2 gyors hatásai virusokra");
    }

    @Test
    @DisplayName("Read a project by id")
    void testGetProjectById(){
        ProjectDto result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}").build(projectDto2.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProjectDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result.getName()).isEqualTo("3D Single Molecule Detection (virtual)");
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2022,9,30));
        assertThat(result.getBudget()).isEqualTo(75);
        assertThat(result.getResearchGroupSet()).hasSize(0);
    }

    @Test
    @DisplayName("Read a project with wrong id")
    void testProjectWithWrongId(){
        Problem result=webTestClient
                .get()
                .uri("/api/projects/-1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Problem.class)
                .returnResult()
                .getResponseBody();

        assertEquals("Project with id: -1 not found",result.getDetail());
        assertEquals(Status.NOT_FOUND,result.getStatus());
        assertEquals("Not found",result.getTitle());
        assertEquals("projects/not-found",result.getType().getPath());
    }

    @Test
    @DisplayName("Update a project")
    void testUpdateResearchGroup(){
        UpdateProjectCommand updateProjectCommand=new UpdateProjectCommand();
        updateProjectCommand.setName("Covid-2022");
        updateProjectCommand.setStartDate(LocalDate.of(2022,1,1));
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/update/{id}").build(projectDto1.getId()))
                .bodyValue(updateProjectCommand)
                .exchange()
                .expectStatus().isOk();

        ProjectDto result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/{id}").build(projectDto1.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProjectDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result.getName()).isEqualTo("Covid-2022");
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2022,1,1));
        assertThat(result.getBudget()).isEqualTo(195);
    }

    @Test
    @DisplayName("Update a project with invalid budget")
    void testUpdateProjectWithInvalidBudget(){
        UpdateProjectCommand updateProjectCommand=new UpdateProjectCommand();
        updateProjectCommand.setBudget(-1);

        Problem result=webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/update/{id}").build(projectDto1.getId()))
                .bodyValue(updateProjectCommand)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(Problem.class)
                .returnResult()
                .getResponseBody();

        assertEquals("Budget musn't be negative!",result.getDetail());
        assertEquals(Status.BAD_REQUEST,result.getStatus());
        assertEquals("Not Valid Parameter",result.getTitle());
        assertEquals("parameter/not-valid",result.getType().getPath());
    }
    @Test
    @DisplayName("Delete a project")
    void testDeleteProject(){
        webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path("/api/projects/delete/{id}").build(projectDto1.getId()))
                .exchange()
                .expectStatus().isNoContent();

        List<ProjectDto> result=webTestClient
                .get()
                .uri("/api/projects")
                .exchange()
                .expectBodyList(ProjectDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result)
                .hasSize(2)
                .contains(new ProjectDto(0L,"E2 gyors hatásai virusokra",LocalDate.of(2019,1,1),145, new HashSet<>()))
                .extracting(ProjectDto::getName)
                .containsExactly("E2 gyors hatásai virusokra","3D Single Molecule Detection (virtual)");
    }
}