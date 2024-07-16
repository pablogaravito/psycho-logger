
# Psycho-Logger

A spring boot application, using MySql and Thymeleaf, meant for psychotherapists and psychologists, used to manage patients and sessions, as well as payments, and birthdays.

## Built With

* [![Spring Boot][Spring Boot]][Spring Boot-url]
* [![Java][Java]][Java-url]
* [![MySQL][MySQL]][MySQL-url]
* [![Maven][Maven]][Maven-url]
* [![Thymeleaf][Thymeleaf]][Thymeleaf-url]
* [![Bootstrap][Bootstrap.com]][Bootstrap-url]
* [![JQuery][JQuery.com]][JQuery-url]

## API Endpoints

| Method | Endpoint                                              | Description                                                                        |
|--------|-------------------------------------------------------|------------------------------------------------------------------------------------|
| GET    | `/api/patients/<id>`                                  | Retrieve a patient                                                                 |
| GET    | `/api/patients`                                       | Retrieve all active patients                                                       |
| GET    | `/api/patients?page=<page>&size=<size>`               | Retrieve active patients with pagination                                           |
| GET    | `/api/patients/all`                                   | Retrieve all patients                                                              |
| POST   | `/api/patients`                                       | Create new patient                                                                 |
| PUT    | `/api/patients/<id>`                                  | Update a patient                                                                   |
| PATCH  | `/api/patients/<id>`                                  | Partial Update a patient                                                           |
| DELETE | `/api/patients/<id>`                                  | Delete a patient                                                                   |
| GET    | `/api/sessions/<id>`                                  | Retrieve a session                                                                 |
| GET    | `/api/sessions?page=<page>&size=<size>`               | Retrieve sessions with pagination                                                  |
| GET    | `/api/sessions`                                       | Retrieve all sessions                                                              |
| GET    | `/api/patients/<id>/sessions`                         | Retrieve a patient's sessions                                                      |
| GET    | `/api/patients/<id>/sessions?page=<page>&size=<size>` | Retrieve a patient's sessions with pagination                                      |
| POST   | `/api/sessions`                                       | Create new session                                                                 |
| PUT    | `/api/sessions/<id>`                                  | Update a session                                                                   |
| PATCH  | `/api/sessions/<id>`                                  | Partial Update a session                                                           |
| DELETE | `/api/sessions/<id>`                                  | Delete a session                                                                   |
| GET    | `/api/debt/`                                          | Retrieve patients with debt                                                        |
| GET    | `/api/debt/<id>`                                      | Retrieve a patient's debt sessions                                                 |
| POST   | `/api/debt/pay`                                       | Pay debt sessions (accepts single sessionId or list of sessionIds in request body) |

## Contact

Pablo Garavito - pablo.garavito@gmail.com

Project Link: [https://github.com/pablogaravito/psycho-logger](https://github.com/pablogaravito/psycho-logger)



[Bootstrap.com]: https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[JQuery.com]: https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white
[JQuery-url]: https://jquery.com
[Spring Boot]: https://img.shields.io/badge/spring%20boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[Spring Boot-url]: https://spring.io/projects/spring-boot
[Java]: https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white
[Java-url]: https://www.oracle.com/java/
[MySQL]: https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white
[MySQL-url]: https://www.mysql.com/
[Maven]: https://img.shields.io/badge/maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white
[Maven-url]: https://maven.apache.org/
[Thymeleaf]: https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white
[Thymeleaf-url]: https://www.thymeleaf.org/