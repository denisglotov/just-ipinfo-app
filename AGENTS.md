# Project Guidelines

- **Formatting:** Always run `./gradlew ktlintFormat` before finalizing changes to ensure code style compliance.
- **Architecture:** Follow Clean Code principles:
  - Keep UI logic in ViewModels (MVVM).
  - Use Repositories for data operations.
  - Avoid logic in Activities/Composables.
- **Style:** Prefer clear, descriptive names over comments.
