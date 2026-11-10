# 🤿 Scuba Dive Log App

A Kotlin console application designed to manage scuba divers and their diving activities.

Developed for the **Software Development Tools** module at South East Technological University (SETU).

## Current Features

- Add a new diver
- Display all divers
- Find a diver by ID
- Update diver details
- Delete a diver
- Automatically generate unique diver IDs
- Validate menu choices (0–5)
- Validate diver name, date of birth, email and certification
- Prevent negative previous dive counts
- Handle invalid user input without crashing
- Log application startup and exit

Each diver has a name, date of birth, email, certification and previous dive count.

## Technologies Used

- **Kotlin** — Application development
- **Gradle** — Build automation
- **IntelliJ IDEA** — Development environment
- **Git & GitHub** — Version control
- **Kotlin Logging & SLF4J** — Application logging

## Project Structure

- `Main.kt` — Console menu and user interaction
- `Diver.kt` — Diver data model
- `DiverService.kt` — Diver management and CRUD operations
- `UserInput.kt` — Input validation and error handling

## How to Run

1. Open the project in IntelliJ IDEA.
2. Run `Main.kt`.
3. The main menu will appear with the following options:
   - **1** — Add Diver
   - **2** — Display All Divers
   - **3** — Update Diver
   - **4** — Delete Diver
   - **5** — Find Diver by ID
   - **0** — Exit the application
4. Enter the number of the option you want to use and follow the instructions displayed.
5. Enter **0** to close the application.

## Testing

All Diver CRUD operations have been manually tested with valid and invalid IDs.

Input validation has also been manually tested with valid and invalid values, including menu choices, dates, emails, names, certifications and previous dive counts.

Application startup and exit logging has been tested successfully.

Automated JUnit tests will be added in a future version.

## Development

The project follows an iterative development approach using GitHub Issues, branches, commits and Pull Requests.
