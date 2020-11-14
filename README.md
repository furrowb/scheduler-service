### Instructions
Create an API using the language of your choosing that will be the foundation for an availability microservice that will help us determine if a time slot is available as well as reserve and free up time slots.
The service should be able to perform 3 main functions:
1. Check availability of a `time_slot` (a `time_slot` is a combination of `Start Timestamp` and `Duration`)
   - **Input:** `time_slot`
   - **Output:** `true` if the time slot is available, `false` if it is not available
2. Reserve availability of a `time_slot`
   - **Input:** `time_slot`
   - **Output:** confirmation if successful, user friendly error if not successful
3. Free availability of a previous time slot
   - **Input:** `time_slot`
   - **Output:** confirmation if successful, user friendly error if not successful
### Notes
- Data storage is up to you
- Testing is nice but not required for this
- If you don't have time to fully implement something leave a comment on how you would approach it.
