# crypto-auto
Prerequisite:
- Timescable db (pg-14) with `timescabledb-toolskit` (Optional)
- Java 11
- update the db details to 
  - `spring.datasource.url`
  - `spring.datasource.username`
  - `spring.datasource.password`
  - `flyway.user`
  - `flyway.password`
  - `flyway.schema`
  - `flyway.url`

Installation:
- `./gradlew build`

Run:
- `./gradlew bootRun --args='--spring.profiles.active=dev'`






