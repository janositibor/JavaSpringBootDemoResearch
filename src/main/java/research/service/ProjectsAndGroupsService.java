package research.service;

import org.modelmapper.TypeToken;
import org.springframework.transaction.annotation.Transactional;
import research.criteria.*;
import research.dtos.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import research.exceptions.*;
import research.model.Location;
import research.model.Project;
import research.model.ResearchGroup;
import research.repository.ResearchGroupsRepository;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class ProjectsAndGroupsService {
    private final ModelMapper modelMapper;
    private final research.repository.ProjectsRepository projectsRepository;
    private final ResearchGroupsRepository researchGroupsRepository;

    private final Validation validation=new Validation();

    private final Type projectDtoListType=new TypeToken<List<ProjectDto>>(){}.getType();
    private final Type researchGroupDtoListType=new TypeToken<List<ResearchGroupDto>>(){}.getType();


    public ProjectsAndGroupsService(ModelMapper modelMapper, research.repository.ProjectsRepository projectsRepository, ResearchGroupsRepository researchGroupsRepository) {
        this.modelMapper = modelMapper;
        this.projectsRepository = projectsRepository;
        this.researchGroupsRepository = researchGroupsRepository;
    }

    public ProjectDto createProject(CreateProjectCommand createProjectCommand) {
        Project project=modelMapper.map(createProjectCommand,Project.class);
        Project validProject=checkProject(project);
        projectsRepository.save(validProject);
        return modelMapper.map(validProject,ProjectDto.class);
    }

    public ResearchGroupDto createResearchGroup(CreateResearchGroupCommand createResearchGroupCommand) {
        ResearchGroup researchGroup=modelMapper.map(createResearchGroupCommand,ResearchGroup.class);
        ResearchGroup validResearchGroup=checkResearchGroup(researchGroup);
        researchGroupsRepository.save(validResearchGroup);
        return modelMapper.map(researchGroup,ResearchGroupDto.class);
    }

    public ProjectDto addPostedGroupToProject(long id, CreateResearchGroupCommand createResearchGroupCommand) {
        ResearchGroup researchGroup=modelMapper.map(createResearchGroupCommand,ResearchGroup.class);
        Project project=findProjectById(id);

        ResearchGroup validResearchGroup=validation.validResearchGroup(researchGroup);
        isSavedResearchGroup(validResearchGroup);

        project.addGroup(researchGroup);
        researchGroupsRepository.save(researchGroup);

        return modelMapper.map(project,ProjectDto.class);
    }

    public ProjectDto addGroupToProject(long projectId, long groupId) {
        ResearchGroup researchGroup=findResearchGroupById(groupId);
        Project project=findProjectById(projectId);
        project.addGroup(researchGroup);
        return modelMapper.map(project,ProjectDto.class);
    }

    public void deleteResearchGroup(long id) {
        ResearchGroup researchGroup=findResearchGroupById(id);
        findProjectsByParticipatingGroup(researchGroup).forEach( project -> project.removeGroup(researchGroup));
        researchGroupsRepository.deleteById(id);
    }

    public List<Project> findProjectsByParticipatingGroup(ResearchGroup researchGroup){
        return projectsRepository.findProjectsByParticipatingGroup(researchGroup);
    }

    public void deleteProject(long id) {
        Project project=findProjectById(id);
        projectsRepository.delete(project);
    }

    public ProjectDto getProjectById(long id) {
        Project project=findProjectById(id);
        return modelMapper.map(project,ProjectDto.class);
    }

    public ResearchGroupDto getResearchGroupById(long id) {
        ResearchGroup researchGroup=findResearchGroupById(id);
        return modelMapper.map(researchGroup,ResearchGroupDto.class);
    }

    public ProjectDto updateProject(long id, UpdateProjectCommand updateProjectCommand) {
        Project project=findProjectById(id);
        if(updateProjectCommand.getName()!=null){
            checkString(updateProjectCommand.getName());
            project.setName(updateProjectCommand.getName());
        }
        if(updateProjectCommand.getStartDate()!=null){
            checkDateForProject(updateProjectCommand.getStartDate());
            project.setStartDate(updateProjectCommand.getStartDate());
        }
        if(updateProjectCommand.getBudget()!=null){
            checkInteger(updateProjectCommand.getBudget(),"Budget musn't be negative!");
            project.setBudget(updateProjectCommand.getBudget());
        }
        return modelMapper.map(project,ProjectDto.class);
    }

    public ResearchGroupDto updateResearchGroupById(long id, UpdateResearchGroupCommand updateResearchGroupCommand) {
        ResearchGroup researchGroup=findResearchGroupById(id);
        if(updateResearchGroupCommand.getName()!=null){
            checkString(updateResearchGroupCommand.getName());
            researchGroup.setName(updateResearchGroupCommand.getName());
        }
        if(updateResearchGroupCommand.getFounded()!=null){
            checkDateForGroup(updateResearchGroupCommand.getFounded());
            researchGroup.setFounded(updateResearchGroupCommand.getFounded());
        }
        if(updateResearchGroupCommand.getCountOfResearchers()!=null){
            checkInteger(updateResearchGroupCommand.getCountOfResearchers(),"Number of researcher musn't be negative!");
            researchGroup.setCountOfResearchers(updateResearchGroupCommand.getCountOfResearchers());
        }
        if(updateResearchGroupCommand.getLocation()!=null){
            checkLocation(updateResearchGroupCommand.getLocation());
            researchGroup.setLocation(updateResearchGroupCommand.getLocation());
        }
        if(updateResearchGroupCommand.getBudget()!=null){
            checkInteger(updateResearchGroupCommand.getBudget(),"Budget musn't be negative!");
            researchGroup.setBudget(updateResearchGroupCommand.getBudget());
        }
        return modelMapper.map(researchGroup,ResearchGroupDto.class);
    }

    public List<ResearchGroupDto> getResearchGroups(ResearchGroupCriteria researchGroupCriteria) {
        List<ResearchGroup> filtered = researchGroupsRepository.findAllByCriteria(researchGroupCriteria.getNameLike(),researchGroupCriteria.getMinCountOfResearchers(),researchGroupCriteria.getMinBudget());
        List<ResearchGroup> result=sortResearchGroups(filtered,researchGroupCriteria);
        return modelMapper.map(result,researchGroupDtoListType);
    }

    public List<ProjectDto> getProjects(ProjectCriteria projectCriteria) {
        List<Project> filtered = projectsRepository.findAllByCriteria(projectCriteria.getNameLike(),projectCriteria.getStartBefore(),projectCriteria.getStartAfter(),projectCriteria.getMinBudget());
        List<Project> result=sortProjects(filtered,projectCriteria);
        return modelMapper.map(result,projectDtoListType);
    }

    public ProjectDto deleteGroupFromProject(long projectId, long groupId) {
        ResearchGroup researchGroup=findResearchGroupById(groupId);
        Project project=findProjectById(projectId);
        project.removeGroup(researchGroup);
        return modelMapper.map(project,ProjectDto.class);
    }

    private Project checkProject(Project project) {
        Project validProject=validation.validProject(project);
        isSavedProject(validProject);
        return validProject;
    }

    private void isSavedProject(Project project) {
        Project result =projectsRepository.findByNameIgnoreCase(project.getName());
        if(result!=null){
            throw new ProjectAlreadyExistsException(project,result.getId());
        }
    }

    private ResearchGroup checkResearchGroup(ResearchGroup researchGroup) {
        ResearchGroup validResearchGroup=validation.validResearchGroup(researchGroup);
        isSavedResearchGroup(validResearchGroup);
        return validResearchGroup;
    }

    private void isSavedResearchGroup(ResearchGroup researchGroup) {
        ResearchGroup result =researchGroupsRepository.findByNameIgnoreCaseAndLocation(researchGroup.getName(),researchGroup.getLocation());
        if(result!=null){
            throw new ResearchGroupAlreadyExistsException(researchGroup,result.getId());
        }
    }

    private Project findProjectById(long id) {
        Optional<Project> optionalProject= projectsRepository.findById(id);
        if(optionalProject.isEmpty()){
            throw  new ProjectNotFoundException(id);
        }
        return optionalProject.get();
    }

    private ResearchGroup findResearchGroupById(long id) {
        Optional<ResearchGroup> optionalResearchGroup= researchGroupsRepository.findById(id);
        if(optionalResearchGroup.isEmpty()){
            throw  new ResearchGroupNotFoundException(id);
        }
        return optionalResearchGroup.get();
    }

    private void checkInteger(Integer intToCheck,String message) {
        if(!validation.checkNotNegativeInteger(intToCheck)){
            throw new ParameterNotValidException(message);
        }
    }

    private void checkDateForProject(LocalDate dateToCheck) {
        if(!validation.checkDate(dateToCheck,Project.FIRST_DATE.minusDays(1),Project.LAST_DATE.plusDays(1))){
            throw new ParameterNotValidException("Not a valid date!");
        }
    }

    private void checkString(String s) {
        if(!validation.checkNotBlankString(s)){
            throw new ParameterNotValidException("Name musn't be blank!");
        }
    }

    private void checkLocation(Location locationToCheck) {
        if(!validation.checkValidLocation(locationToCheck)){
            throw new ParameterNotValidException("Location is not valid!");
        }
    }

    private void checkDateForGroup(LocalDate dateToCheck) {
        if(!validation.checkDate(dateToCheck,Project.FIRST_DATE.minusDays(1), LocalDate.now().plusDays(1))){
            throw new ParameterNotValidException("Not a valid date!");
        }
    }

    private List<ResearchGroup> sortResearchGroups(List<ResearchGroup> researchGroupList,ResearchGroupCriteria researchGroupCriteria){
        List<ResearchGroup> orderedResearchGroupList;
        ResearchGroupOrderBy orderBy=researchGroupCriteria.getOrderBy();
        if(orderBy== ResearchGroupOrderBy.budget){
            orderedResearchGroupList=researchGroupOrderByBudget(researchGroupList);
        }
        else if(orderBy== ResearchGroupOrderBy.countOfResearchers){
            orderedResearchGroupList=researchGroupOrderByCountOfResearchers(researchGroupList);
        }
        else if(orderBy== ResearchGroupOrderBy.founded){
            orderedResearchGroupList=researchGroupOrderByFounded(researchGroupList);
        }
        else if(orderBy== ResearchGroupOrderBy.name){
            orderedResearchGroupList=researchGroupOrderByName(researchGroupList);
        }
        else{
            orderedResearchGroupList=researchGroupOrderById(researchGroupList);
        }

        if(researchGroupCriteria.getOrderType()== OrderType.desc){
            Collections.reverse(orderedResearchGroupList);
        }
        return orderedResearchGroupList;
    }

    private List<ResearchGroup> researchGroupOrderById(List<ResearchGroup> researchGroupList) {
        return  researchGroupList.stream()
                .sorted(Comparator.comparing(ResearchGroup::getId))
                .collect(Collectors.toList());
    }

    private List<ResearchGroup> researchGroupOrderByName(List<ResearchGroup> researchGroupList) {
        return  researchGroupList.stream()
                .sorted(Comparator.comparing(ResearchGroup::getName))
                .collect(Collectors.toList());
    }

    private List<ResearchGroup> researchGroupOrderByFounded(List<ResearchGroup> researchGroupList) {
        return  researchGroupList.stream()
                .sorted(Comparator.comparing(ResearchGroup::getFounded))
                .collect(Collectors.toList());
    }

    private List<ResearchGroup> researchGroupOrderByCountOfResearchers(List<ResearchGroup> researchGroupList) {
        return  researchGroupList.stream()
                .sorted(Comparator.comparing(ResearchGroup::getCountOfResearchers))
                .collect(Collectors.toList());
    }

    private List<ResearchGroup> researchGroupOrderByBudget(List<ResearchGroup> researchGroupList) {
        return  researchGroupList.stream()
                .sorted(Comparator.comparing(ResearchGroup::getBudget))
                .collect(Collectors.toList());
    }

    private List<Project> sortProjects(List<Project> projectList, ProjectCriteria projectCriteria) {
        List<Project> orderedProjectList;
        ProjectOrderBy orderBy=projectCriteria.getOrderBy();
        if(orderBy== ProjectOrderBy.budget){
            orderedProjectList=projectOrderByBudget(projectList);
        }
        else if(orderBy== ProjectOrderBy.startDate){
            orderedProjectList=projectOrderByStartDate(projectList);
        }
        else if(orderBy== ProjectOrderBy.name){
            orderedProjectList=projectOrderByName(projectList);
        }
        else{
            orderedProjectList=projectOrderById(projectList);
        }

        if(projectCriteria.getOrderType()== OrderType.desc){
            Collections.reverse(orderedProjectList);
        }
        return orderedProjectList;
    }

    private List<Project> projectOrderById(List<Project> projectList) {
        return  projectList.stream()
                .sorted(Comparator.comparing(Project::getId))
                .collect(Collectors.toList());}

    private List<Project> projectOrderByName(List<Project> projectList) {
        return  projectList.stream()
                .sorted(Comparator.comparing(Project::getName))
                .collect(Collectors.toList());}

    private List<Project> projectOrderByStartDate(List<Project> projectList) {
        return  projectList.stream()
                .sorted(Comparator.comparing(Project::getStartDate))
                .collect(Collectors.toList());}

    private List<Project> projectOrderByBudget(List<Project> projectList) {
        return  projectList.stream()
                .sorted(Comparator.comparing(Project::getBudget))
                .collect(Collectors.toList());
    }
}
