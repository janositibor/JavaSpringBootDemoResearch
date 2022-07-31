CREATE TABLE project_researchgroup
(
    project_id       BIGINT NOT NULL,
    researchgroup_id BIGINT NOT NULL,
    CONSTRAINT pk_project_researchgroup PRIMARY KEY (project_id, researchgroup_id)
);

CREATE TABLE projects
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    project_name VARCHAR(255) NULL,
    start_date   date NULL,
    budget       INT NULL,
    CONSTRAINT pk_projects PRIMARY KEY (id)
);

CREATE TABLE research_groups
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    researchgroup_name   VARCHAR(255) NULL,
    founded              date NULL,
    count_of_researchers INT NULL,
    location             VARCHAR(255) NULL,
    budget               INT NULL,
    CONSTRAINT pk_research_groups PRIMARY KEY (id)
);

ALTER TABLE project_researchgroup
    ADD CONSTRAINT fk_prores_on_project FOREIGN KEY (project_id) REFERENCES projects (id);

ALTER TABLE project_researchgroup
    ADD CONSTRAINT fk_prores_on_research_group FOREIGN KEY (researchgroup_id) REFERENCES research_groups (id);