CREATE SCHEMA IF NOT EXISTS research
DEFAULT CHARACTER SET utf8
COLLATE utf8_hungarian_ci;
CREATE USER researchUser@localhost IDENTIFIED BY 'researchPass';
GRANT ALL ON research.* TO researchUser@localhost;
USE research;

CREATE SCHEMA IF NOT EXISTS research_test
    DEFAULT CHARACTER SET utf8
    COLLATE utf8_hungarian_ci;
GRANT ALL ON research_test.* TO researchUser@localhost;
USE research_test;