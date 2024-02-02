# Demo project

## Feladat ismertetése

A megvalósított alkalmazás projektek és kutatócsoportok kapcsolatát kezeli.

Az alkalmazás Java Spring Boot keretrendszerrel megvalósított backend, amely a klaszikus három réteggel rendelkezik:
- a külvilággal a controller rétegben megvalósított RESTful webszolgáltatás végpontjain keresztül kommunikál
- a MariaDB adatbázissal a Spring Data JPA-val megvalósított repository réteg tartja a kapcsolatot
- a kettő közt helyezkedik el az üzleti logikát megvalósító service réteg

## Felépítés

### ResearchGroup

A `ResearchGroup` entitás a következő attribútumokkal rendelkezik:

* id (Long)
* name (String, nem Blank)
* founded (LocalDate, nem null, 1899-12-31 és az aktuális dátum közti értéket vár)
* countOfResearchers (int, pozitív)
* location (Location enum típusú)
* budget (int, nem negatív, pénzügyi keret millió HUF-ban)
* projectSet (Set, a projektek halmaza amiben részt vesz a csoport)

Végpontok: 

| HTTP metódus | Végpont                 			  | Leírás                                                        |
| ------------ | ------------------------------------ | ------------------------------------------------------------- |
| POST         | `"/api/research-groups"`        	  | létrehozza a kutatócsoportot                                  |
| GET          | `"/api/research-groups"`        	  | rendezve adja vissza az összes vagy a szűrt kutatócsoportokat |
| GET          | `"/api/research-groups/{id}"`   	  | lekérdez egy kutatócsoportot `id` alapján                     |
| PUT          | `"/api/research-groups/update/{id}"` | frissíti a kutatócsoport adatait                              |
| DELETE       | `"/api/research-groups/delete/{id}"` | törli a kutatócsoportot                                       |

Kutatócsoport létrehozásakor ellenőrzi a alkalmazás, hogy az érkező adatok érvényesek-e. Ha ez nem teljesül 400-as Bad Request kóddal tér vissza.
Ha a kutatócsoport már létezik 409-es Conflict hibaüzenetet ad a alkalmazás.
 
A kutatócsoportok szűréséhez query paraméterek használhatóak.
Szűrni a következő paraméterek tetszőleges kombinációjával lehet:
* nameLike (String, részleges egyezés)
* minCountOfResearchers (int)
* minBudget (int)

Az eredmények rendezését (`orderBy`) a következő attribútumok alapján
* id
* name
* founded
* countOfResearchers
* budget
növekvő vagy csökkenő sorrendben (`OrderType`) lehet végrehajtani.

### Project 

A `Project ` entitás a következő attribútumokkal rendelkezik:

* id (Long)
* name (String, nem Blank)
* startDate (LocalDate, nem null, 1899-12-31 és 2100-12-31 közti értéket vár)
* budget (int, nem negatív, pénzügyi keret millió HUF-ban)
* researchGroupSet (Set, a projekben résztvevő csoportok halmaza)

A `Project` és a `ResearchGroup` entitások között kétirányú, m-n kapcsolat van.

Végpontok:

| HTTP metódus | Végpont                 			 | Leírás                                                  |
| ------------ | ----------------------------------- | ------------------------------------------------------- |
| POST         | `"/api/projects"`        			 | létrehoz egy projektet                                  |
| GET          | `"/api/projects"`        			 | rendezve adja vissza az összes vagy a szűrt projekteket |
| GET          | `"/api/projects/{id}"`   			 | lekérdez egy projektet `id` alapján                     |
| PUT          | `"/api/projects/update/{id}"`   	 | frissíti a projekt adatait                              |
| POST         | `"/api/projects/{id}/add-group"`    | új kutatócsoportot add a projekthez                     |
| GET          | `"/api/projects/{id}/add-group"`    | már létező kutatócsoportot add a projekthez             |
| GET          | `"/api/projects/{id}/delete-group"` | kutatócsoport eltávolítása a projektből                 |
| DELETE       | `"/api/projects/delete/{id}"`   	 | törli a projektet                                       |


Projekt létrehozásakor ellenőrzi a alkalmazás, hogy az érkező adatok érvényesek-e. Ha ez nem teljesül 400-as Bad Request kóddal tér vissza.
Ha a projekt már létezik 409-es Conflict hibaüzenetet ad a alkalmazás.

Kutatócsoport létrehozásakor figyeli, hogy az adatok érvényesek-e, illetve hogy a csoport nem szerepel-e már az adatbázisban.
 
A kutatócsoportokat szűréséhez query paraméterek használhatóak.
Szűrni a következő paraméterek tetszőleges kombinációjával lehet:
* nameLike (String, részleges egyezés)
* startBefore (LocalDate)
* startAfter (LocalDate)
* minBudget (int)

Az eredmények rendezését (`orderBy`) a következő attribútumok alapján
* id
* name
* startDate
* budget
növekvő vagy csökkenő sorrendben (`OrderType`) lehet végrehajtani.

## Technológiai részletek

* Klasszikus háromrétegű alkalmazást valósítottam meg Java Spring backenddel és RESTful webszolgáltatásokkal amely MariaDB adatbázisban tárolja az adatokat.
* Az SQL adatbázist kezelő réteget (`Repository`) Spring Data JPA-val valósítottam meg.
* Az adatbázis inicializálását `Flyway` script végzi.
* Az üzleti logika réteg megvalósítása a `Service` osztály feladata.
* A REST szolgáltatásokat a Controller réteg valósítja meg. 
* A hibák kezelésére saját kivételeket használok (RFC 7807 Problem Details for HTTP APIs, szabványnak megfelelő) amelyeket a a Problem nevű third party library segítségével hozok létre
* Az adatokat már a CreateCommand-okban `Bean Validation` segítségével ellenőrzöm
* `Swagger UI` hozza létre az interaktív dokumentációs felületet
* `WebClient`-tel végeztem az integrációs tesztlést, ami a kód sorainak 82%-át lefedi.
* További manuális tesztelést a *.http fileok tesznek lehetővé
* Az alkalmazás futtatásához Docker hálózatot hoztam létre, amely tartalmazza a konténereket amelyekben a MariaDB adatbázis és a hozzá kapcsolodó alkalmazás futtnak.