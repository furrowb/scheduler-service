# Compromises

This file documents some compromises made in order to not take excessive time on the design of the project or for another reason that will be explicitly noted.

## ReserveRequest

- The user field I would prefer to be an ID with a user table to back it up but that seems slightly outside the scope of the project. For now, I will be using it as a user's name

## AvailabilityController

- While the specs don't ask for it, I would like to do have an endpoint that supports retrieving the scheduling data based on parameters