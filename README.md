# AI Commentator
AI Commentator for IntelliJ IDE

- [Description](#description)
- [Features](#features)
- [How to Run](#how-to-run)
- [Configuration](#configuration)
- [License](#license)


## Description
<!-- Plugin description -->
This plugin provides automated code comment generation using LLM models like OpenAI and Anthropic. It simplifies the process of documenting your code by generating concise and useful comments for classes and methods. The plugin supports multiple languages and models, making it a flexible solution for your documentation needs.
<!-- Plugin description end -->

## Features
- **Class Comment Generation**: Automatically generates a KDoc (for Kotlin) or JavaDoc (for Java) comment for a selected class.
- **Method Comment Generation**: Generates a clear and concise comment for methods in KDoc or JavaDoc format.
- **Streaming Support**: Enables real-time comment generation, updating the comment character-by-character as it is produced by the model.
- **Multi-Model Support**: Choose between OpenAI and Anthropic LLM models to generate comments based on your preference.
- **Extensible Design**: The plugin is structured to easily support new LLM models and additional programming languages with minimal effort.


## Supported models

- Anthropic
- Open AI

## Supported programming languages

- Kotlin
- Java

## How to run
> **Note**: The plugin is currently under development. To run the plugin, you can use the `Run Plugin` Gradle task.
1. Clone this repository.
2. Open the project in IntelliJ IDEA.
3. Run the plugin using the `Run Plugin` task in the Gradle tool window.
4. Configure the plugin with your preferred LLM model (OpenAI or Anthropic) through the settings menu.
5. Start generating comments with just a few clicks!


## Configuration
To configure the plugin:

1. Navigate to <kbd>Settings</kbd> -> <kbd>Tools</kbd> -> <kbd>AiCommentator</kbd>.
2. Select the desired LLM model (<kbd>Anthropic</kbd> or <kbd>OpenAI</kbd>) and enter the necessary credentials.


## License

Please see [LICENSE](LICENSE) for details.