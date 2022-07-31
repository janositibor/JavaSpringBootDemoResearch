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
@Sql(statements = {"DELETE FROM research_groups"})
public class ResearchGroupControllerWebClientIT {
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ProjectsAndGroupsService service;

    ResearchGroupDto researchGroupDto;

    @BeforeEach
    void init(){
        webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("NAP kutatócsoport", LocalDate.of(2018,9,1),15, Location.SZENTÁGOTHAI_KUTATÓKÖZPONT,5))
                .exchange();
        researchGroupDto=webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("FEMTO-Lézeres Csoport",LocalDate.of(2016,6,1),7,Location.BIOFIZIKA,15))
                .exchange()
                .expectBody(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();
        webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("Nano-Bio-Imaging Core Facility",LocalDate.of(2019,7,19),3,Location.ÁOK_ÚJ_ÉPÜLET,13))
                .exchange();
        webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("Neuroendokrinológia",LocalDate.of(2019,1,13),15,Location.ÁOK_RÉGI_ÉPÜLET,14))
                .exchange();
    }

    @Test
    @DisplayName("Create a research group")
    void testCreateResearchGroup(){
        ResearchGroupDto created=webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("Virológia",LocalDate.of(2016,7,1),8,Location.SZENTÁGOTHAI_KUTATÓKÖZPONT,55))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(created.getName()).isEqualTo("Virológia");
        assertThat(created.getFounded()).isEqualTo(LocalDate.of(2016,7,1));
        assertThat(created.getCountOfResearchers()).isEqualTo(8);
        assertThat(created.getLocation()).isEqualTo(Location.SZENTÁGOTHAI_KUTATÓKÖZPONT);
        assertThat(created.getBudget()).isEqualTo(55);
        assertThat(created.getProjectSet()).hasSize(0);
    }

    @Test
    @DisplayName("Create and read a research group")
    void testCreateAndReadResearchGroup(){
        ResearchGroupDto created=webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("Virológia",LocalDate.of(2016,7,1),8,Location.SZENTÁGOTHAI_KUTATÓKÖZPONT,55))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();

        ResearchGroupDto result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/research-groups/{id}").build(created.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result.getName()).isEqualTo("Virológia");
        assertThat(result.getFounded()).isEqualTo(LocalDate.of(2016,7,1));
        assertThat(result.getCountOfResearchers()).isEqualTo(8);
        assertThat(result.getLocation()).isEqualTo(Location.SZENTÁGOTHAI_KUTATÓKÖZPONT);
        assertThat(result.getBudget()).isEqualTo(55);
        assertThat(result.getProjectSet()).hasSize(0);
    }

    @Test
    @DisplayName("Create a research group with invalid name")
    void testCreateResearchGroupWithInvalidName() {
        ConstraintViolationProblem result=webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand(" ",LocalDate.of(2016,7,1),8,Location.SZENTÁGOTHAI_KUTATÓKÖZPONT,55))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ConstraintViolationProblem.class)
                .returnResult()
                .getResponseBody();

        assertEquals(Status.BAD_REQUEST,result.getStatus());
        assertEquals("name",result.getViolations().get(0).getField());
        assertEquals("The name of the research group mustn't be blank!",result.getViolations().get(0).getMessage());
    }

    @Test
    @DisplayName("Create a research group with no founding date")
    void testCreateResearchGroupWithNoFounded() {
        ConstraintViolationProblem result=webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("Virológia",null,8,Location.SZENTÁGOTHAI_KUTATÓKÖZPONT,55))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ConstraintViolationProblem.class)
                .returnResult()
                .getResponseBody();

        assertEquals(Status.BAD_REQUEST,result.getStatus());
        assertEquals("founded",result.getViolations().get(0).getField());
        assertEquals("The research group's date of foundation must be valid!",result.getViolations().get(0).getMessage());
    }

    @Test
    @DisplayName("Create a research group with invalid date")
    void testCreateResearchGroupWithInvalidDate() {
        Problem result=webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("Virológia",LocalDate.of(1016,7,1),8,Location.SZENTÁGOTHAI_KUTATÓKÖZPONT,55))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(Problem.class)
                .returnResult()
                .getResponseBody();

        assertThat(result.getDetail())
                .contains("ResearchGroup (name:Virológia, founded:")
                .contains(", location:SZENTÁGOTHAI_KUTATÓKÖZPONT) not valid.");
        assertEquals(Status.BAD_REQUEST,result.getStatus());
        assertEquals("Not Valid",result.getTitle());
        assertEquals("research-groups/not-valid",result.getType().getPath());
    }

    @Test
    @DisplayName("Create a research group which already exists")
    void testCreateResearchGroupAlreadyExists() {
        Problem result=webTestClient
                .post()
                .uri("/api/research-groups")
                .bodyValue(new CreateResearchGroupCommand("FEMTO-Lézeres Csoport",LocalDate.of(2016,6,1),7,Location.BIOFIZIKA,15))
                .exchange()
                .expectBody(Problem.class)
                .returnResult()
                .getResponseBody();

        assertEquals("Group (name:FEMTO-Lézeres Csoport) already exists with id: "+researchGroupDto.getId(),result.getDetail());
        assertEquals(Status.CONFLICT,result.getStatus());
        assertEquals("Already Exists",result.getTitle());
        assertEquals("research-groups/already-exists",result.getType().getPath());
    }

    @Test
    @DisplayName("Read all research groups")
    void testGetAllResearchGroups(){
        List<ResearchGroupDto> result=webTestClient
                .get()
                .uri("/api/research-groups")
                .exchange()
                .expectBodyList(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result)
            .hasSize(4)
            .contains(new ResearchGroupDto(1L,"Nano-Bio-Imaging Core Facility",LocalDate.of(2019,7,19),3, Location.ÁOK_ÚJ_ÉPÜLET,13,new HashSet<>()))
            .extracting(ResearchGroupDto::getName)
            .containsOnly("NAP kutatócsoport","FEMTO-Lézeres Csoport","Nano-Bio-Imaging Core Facility","Neuroendokrinológia");
    }

    @Test
    @DisplayName("Read filtered and sorted research groups (1)")
    void testFilteredResearchGroups(){
        List<ResearchGroupDto> result=webTestClient
                .get()
                .uri(builder -> builder.path("/api/research-groups")
                        .queryParam("minCountOfResearchers","4")
                        .queryParam("minBudget","14")
                        .queryParam("orderBy","founded")
                        .queryParam("OrderType","asc")
                        .build())
                .exchange()
                .expectBodyList(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result)
                .hasSize(2)
                .extracting(ResearchGroupDto::getName)
                .containsExactly("FEMTO-Lézeres Csoport","Neuroendokrinológia");
    }
@Test
    @DisplayName("Read filtered and sorted research groups (2)")
    void testFilteredResearchGroupsV2(){
        List<ResearchGroupDto> result=webTestClient
                .get()
                .uri(builder -> builder.path("/api/research-groups")
                        .queryParam("minCountOfResearchers","4")
                        .queryParam("orderBy","id")
                        .queryParam("OrderType","desc")
                        .build())
                .exchange()
                .expectBodyList(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result)
                .hasSize(3)
                .extracting(ResearchGroupDto::getName)
                .containsExactly("Neuroendokrinológia","FEMTO-Lézeres Csoport","NAP kutatócsoport");
    }

    @Test
    @DisplayName("Read a research group by id")
    void testGetResearchGroupById(){
        ResearchGroupDto result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/research-groups/{id}").build(researchGroupDto.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result.getName()).isEqualTo("FEMTO-Lézeres Csoport");
        assertThat(result.getFounded()).isEqualTo(LocalDate.of(2016,6,1));
        assertThat(result.getCountOfResearchers()).isEqualTo(7);
        assertThat(result.getLocation()).isEqualTo(Location.BIOFIZIKA);
        assertThat(result.getBudget()).isEqualTo(15);
        assertThat(result.getProjectSet()).hasSize(0);
    }

    @Test
    @DisplayName("Read a research group with wrong id")
    void testReadResearchGroupWithWrongId(){
        Problem result=webTestClient
                .get()
                .uri("/api/research-groups/-1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Problem.class)
                .returnResult()
                .getResponseBody();

        assertEquals("Research group with id: -1 not found",result.getDetail());
        assertEquals(Status.NOT_FOUND,result.getStatus());
        assertEquals("Not found",result.getTitle());
        assertEquals("research-groups/not-found",result.getType().getPath());
    }

    @Test
    @DisplayName("Update a research group")
    void testUpdateResearchGroup(){
        UpdateResearchGroupCommand updateResearchGroupCommand=new UpdateResearchGroupCommand();
        updateResearchGroupCommand.setName("FemtoSzekundumos-Lézeres Csoport");
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder.path("/api/research-groups/update/{id}").build(researchGroupDto.getId()))
                .bodyValue(updateResearchGroupCommand)
                .exchange()
                .expectStatus().isOk();

        ResearchGroupDto result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/research-groups/{id}").build(researchGroupDto.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result.getName()).isEqualTo("FemtoSzekundumos-Lézeres Csoport");
        assertThat(result.getFounded()).isEqualTo(LocalDate.of(2016,6,1));
        assertThat(result.getCountOfResearchers()).isEqualTo(7);
        assertThat(result.getLocation()).isEqualTo(Location.BIOFIZIKA);
        assertThat(result.getBudget()).isEqualTo(15);
        assertThat(result.getProjectSet()).hasSize(0);
    }

    @Test
    @DisplayName("Update a research group with invalid budget")
    void testUpdateResearchGroupWithInvalidBudget(){
        UpdateResearchGroupCommand updateResearchGroupCommand=new UpdateResearchGroupCommand();
        updateResearchGroupCommand.setBudget(-1);

        Problem result=webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder.path("/api/research-groups/update/{id}").build(researchGroupDto.getId()))
                .bodyValue(updateResearchGroupCommand)
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
    @DisplayName("Delete a research group")
    void testDeleteResearchGroup(){
        webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path("/api/research-groups/delete/{id}").build(researchGroupDto.getId()))
                .exchange()
                .expectStatus().isNoContent();

        List<ResearchGroupDto> result=webTestClient
                .get()
                .uri("/api/research-groups")
                .exchange()
                .expectBodyList(ResearchGroupDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result)
                .hasSize(3)
                .contains(new ResearchGroupDto(1L,"Nano-Bio-Imaging Core Facility",LocalDate.of(2019,7,19),3, Location.ÁOK_ÚJ_ÉPÜLET,13,new HashSet<>()))
                .extracting(ResearchGroupDto::getName)
                .doesNotContain("FEMTO-Lézeres Csoport")
                .containsOnly("NAP kutatócsoport","Nano-Bio-Imaging Core Facility","Neuroendokrinológia");
    }
}
