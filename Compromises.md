# Compromises

This file documents some compromises made in order to not take excessive time on the design of the project or for another reason that will be explicitly noted.

## Nice-to-haves

- I would have preferred to Dockerize the project and then possibly set it up with another database like Postgres.
- I'd prefer to have more testing:
    - Around the ReservationService and mocking out the components instead of doing an integration test.
    - Separate tests for integration tests.
    - Testing around the expected output from the REST Controller would have been nice. That way, we could ensure that the JSON response is exactly as expected.
    - Intellij reports the code coverage around 80-90%, but it seems mostly due to the models, and the top-level application class. For the latter, I would just need to create a test class that runs the main application.
- An audit log. Right now, we have no way to know who did what or when so using something like event-sourcing plus having authenticated users would be nice.
- Logging. Currently, I didn't add any logging other than what Spring Boot provides. I would have liked to use something like sl4j to handle this.
- A linter. I didn't add a linter, but I'd prefer one to have the project be more consistent and find code smells.

## ReserveRequest

- The user field I would prefer to be an ID with a user table to back it up but that seems slightly outside the scope of the project. For now, I will be using it as a user's name.
- While the validators do a decent job testing the data can be translated into an object (like OffSetDateTime) it doesn't check if the time is in the past, present, or future. I would like to write a custom validator to ensure that the date being reserved is in the future.

## ReservationController

- While the specs don't ask for it, I would like to do have an endpoint that supports retrieving a reservation based on the time or user.
- The controller currently doesn't show a message on WHY some things fail, like a bad field. While there is validation, and the validation knows exactly which field is having an issue, the exceptions thrown are wrapped up and caught elsewhere by Spring. I'm sure there's a way to retrieve them, but I would have to investigate it further.

## Reservation (entity class)

- The Reservation class has a few issues:
    - The table name isn't taking hold and JPA is relying on the class name instead. There's supposedly a way around this, but I would need to investigate it further.
    - Kotlin (and myself) prefer not to have open classes/methods but JPA is more used to Java than Kotlin and requires these things to properly set values.
