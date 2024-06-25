# Demo Project

## Task Description

The implemented application manages the relationship between projects and research groups.

The application is a Java Spring Boot framework backend, featuring the classic three-layer architecture:
- It communicates with the external world through RESTful web service endpoints implemented in the controller layer.
- It connects to the MariaDB database through the repository layer implemented with Spring Data JPA.
- The service layer, which implements business logic, is situated between these two layers.

## Structure

### ResearchGroup

The `ResearchGroup` entity has the following attributes:

* id (Long)
* name (String, not Blank)
* founded (LocalDate, not null, expected value between 1899-12-31 and the current date)
* countOfResearchers (int, positive)
* location (Location enum type)
* budget (int, non-negative, financial budget in million HUF)
* projectSet (Set, the set of projects in which the group participates)

Endpoints:

| HTTP Method | Endpoint                 			 | Description                                     |
| ----------- | ------------------------------------ | ----------------------------------------------- |
| POST        | `"/api/research-groups"`        	 | Creates a research group                        |
| GET         | `"/api/research-groups"`        	 | Returns all or filtered research groups, sorted |
| GET         | `"/api/research-groups/{id}"`   	 | Retrieves a research group by `id`              |
| PUT         | `"/api/research-groups/update/{id}"` | Updates the details of a research group         |
| DELETE      | `"/api/research-groups/delete/{id}"` | Deletes a research group                        |

When creating a research group, the application checks if the incoming data is valid. If not, it returns a 400 Bad Request code.
If the research group already exists, the application returns a 409 Conflict error.

Query parameters can be used to filter research groups.
Filtering can be done with any combination of the following parameters:
* nameLike (String, partial match)
* minCountOfResearchers (int)
* minBudget (int)

Sorting results (`orderBy`) can be performed based on the following attributes:
* id
* name
* founded
* countOfResearchers
* budget
in ascending or descending order (`OrderType`).

### Project

The `Project` entity has the following attributes:

* id (Long)
* name (String, not Blank)
* startDate (LocalDate, not null, expected value between 1899-12-31 and 2100-12-31)
* budget (int, non-negative, financial budget in million HUF)
* researchGroupSet (Set, the set of groups participating in the project)

There is a bidirectional many-to-many relationship between `Project` and `ResearchGroup` entities.

Endpoints:

| HTTP Method | Endpoint                 		    | Description                                    |
| ----------- | ----------------------------------- | ---------------------------------------------- |
| POST        | `"/api/projects"`        			| Creates a project                              |
| GET         | `"/api/projects"`        			| Returns all or filtered projects, sorted       |
| GET         | `"/api/projects/{id}"`   			| Retrieves a project by `id`                    |
| PUT         | `"/api/projects/update/{id}"`   	| Updates the details of a project               |
| POST        | `"/api/projects/{id}/add-group"`    | Adds a new research group to the project       |
| GET         | `"/api/projects/{id}/add-group"`    | Adds an existing research group to the project |
| GET         | `"/api/projects/{id}/delete-group"` | Removes a research group from the project      |
| DELETE      | `"/api/projects/delete/{id}"`   	| Deletes a project                              |


When creating a project, the application checks if the incoming data is valid. If not, it returns a 400 Bad Request code.
If the project already exists, the application returns a 409 Conflict error.

When creating a research group, the application checks if the data is valid and whether the group already exists in the database.

Query parameters can be used to filter projects.
Filtering can be done with any combination of the following parameters:
* nameLike (String, partial match)
* startBefore (LocalDate)
* startAfter (LocalDate)
* minBudget (int)

Sorting results (`orderBy) can be performed based on the following attributes:
* id
* name
* startDate
* budget
in ascending or descending order (`OrderType`).

Technological Details

* I implemented a classic three-layer application with a Java Spring backend and RESTful web services that store data in a MariaDB database.
* The SQL database management layer (Repository) is implemented with Spring Data JPA.
* Database initialization is handled by Flyway scripts.
* The business logic layer is implemented in the Service class.
* REST services are implemented in the Controller layer.
* Custom exceptions are used for error handling (compliant with RFC 7807 Problem Details for HTTP APIs), created using the Problem third-party library.
* Data validation is performed in the CreateCommands using Bean Validation.
* Swagger UI generates the interactive documentation interface.
* Integration testing was conducted with WebClient, covering 82% of the code lines.
* Additional manual testing is facilitated by *.http files.
* To run the application, I created a Docker network that includes containers running the MariaDB database and the associated application.
* Continuous Integration (CI) is implemented with GitHub Workflows.