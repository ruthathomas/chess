# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Sequence Diagram URLs

[Presentation Mode](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5ks9K+KDvvorxLAC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatLlqOJpEuGFocjA3K8gagrCjAQGhqRbqdph5Q0doMAAGa+LymxgCA6QOPRRHiphGo4Sy5R8QJVSgZ+hEjm+MAfksJFMsxkYUQAapQSCcWEKnvDAyQZKkdoCtojHqdKSaXjB5SGba0mpGo5lQIKjmOs6yaQSWyE8tmuaYP+ILQSUVxTEBKxfNOs7NhMkzfleoWFNkPYwP2g69JFIn3H0MVBnF84JTAS6cKu3h+IEXgoOge4Hr4zDHukmSYGlF5FNQ17SAAoruPX1D1zQtA+qhPt0sWNug7a-mcQIlpNc7Bb5s2sTA8H2I1SENb6qEYhhcpYQSEmkjA5JgLG-qLegalmhGhSWjAOlQHpYTXUkJl+jAl0Oq6NlheCMDvTAJR6agK5idhTGSUYKAIJdAaFVNaC3WRmnlNIcMUoYwOcd4wzfUG8aHZ2IXlMhjWBQgeYrSxyVXElXUpV2ORgH2A5DmVK6eJVG6Qrau7QjAADio6ss1p5teezBhdewsDcN9ijhNSNLT+bJk0DqvTSFbJrcgsSi6MqhIdCRtqHt6FeYmx3Q6d50I+9qMaQ92m6fpWt1sjxmmYTcaWX95q2Z1Tryp7M7e6DnHg9bR0yCd7pnRS5sm87Nmu+Ud1A8qIujsTMGkyt5Nm2LVM0-NzM-hFfRK8b4yVP0tcoAAktI9coAAHmK2DcAAjL2ADMAAsTwnpkBoVgl0zdH0OgIKADYT6O0VPE3AByo4JXsMCNIzxzB+1bMZRz2U12L9cVI3o6t+3XfDD3KD98Po8tfquUrzPc8L0vowr1M6+b3ytvXeZhyo83XIEbAPgoAP3gLqTIudRgpFflLVmet6aVFqA0RWytgja2fL0ABow94dh8hXco70hz-1HBvX+VZO7d24LQscEEK7oPsjAT0epzawjgPAlA5tLZYljuJO2icHZBkRl7Ocacg4Zyeu7N6+CfZfR+gHBOdMOHAyjjHSGttrKnWADaHhTcQyB3uuyTONpEEoHzqHLCZDUzlD4V6TIgi1BBV1sHKupZqGjBvuUBh98+6DyHqVGanZD7syyj0PxLc26BLvg-J+YSuYVQgQESwcN4LJBgAAKQgDyGxgQv4gAbKg8w7DwqYMpHeFoTcVbSPQEOHuwAslQDgBAeCUBZhN1biQ2amtKG9Fae0zp3TenX2kKw1MVTAYACtCloB4QUnk7i0T7REVDAx4iKSO3wbIixj1nqvXDkVFRZk1HyCslnbxh0KHKJ0SgCGJNtl3TwmAExUzDnkXKFRW05taJhD6dIG5aMAZh0BRxbiPheJyAEjyRwIKtn6PefRbAWg3GjlhCCyZowzEaPRnyDFeobGCjkGdKZFzKX4sspDRxwJyirOWaOMuy02F3KZgzCJB9pbRM5qA7ma4qoBC8G0rsXpYDAGwD3Qg8REjIMlofKp3U+oDSGq0YwPK-xF3ZbMiFsFOHcDwIyXhxr3LaCEQdAubzwzlA4HDU1PyiWY24Ag4AOd2LXPMb8mQWN3U5yucAMFLtLF+rdTjHOULrn0p1eQiVJrLUeOpnqqCssyg9AGZEvlx8YlpKAA)

[Default](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5ks9K+KDvvorxLAC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatLlqOJpEuGFocjA3K8gagrCjAQGhqRbqdph5Q0doMAAGa+LymxgCA6QOPRRHiphGo4Sy5R8QJVSgZ+hEjm+MAfksJFMsxkYUQAapQSCcWEKnvDAyQZKkdoCtojHqdKSaXjB5SGba0mpGo5lQIKjmOs6yaQSWyE8tmuaYP+ILQSUVxTEBKxfNOs7NhMkzfleoWFNkPYwP2g69JFIn3H0MVBnF84JTAS6cKu3h+IEXgoOge4Hr4zDHukmSYGlF5FNQ17SAAoruPX1D1zQtA+qhPt0sWNug7a-mcQIlpNc7Bb5s2sTA8H2I1SENb6qEYhhcpYQSEmkjA5JgLG-qLegalmhGhSWjAOlQHpYTXUkJl+jAl0Oq6NlheCMDvTAJR6agK5idhTGSUYKAIJdAaFVNaC3WRmnlNIcMUoYwOcd4wzfUG8aHZ2IXlMhjWBQgeYrSxyVXElXUpV2ORgH2A5DmVK6eJVG6Qrau7QjAADio6ss1p5teezBhdewsDcN9ijhNSNLT+bJk0DqvTSFbJrcgsSi6MqhIdCRtqHt6FeYmx3Q6d50I+9qMaQ92m6fpWt1sjxmmYTcaWX95q2Z1Tryp7M7e6DnHg9bR0yCd7pnRS5sm87Nmu+Ud1A8qIujsTMGkyt5Nm2LVM0-NzM-hFfRK8b4yVP0tcoAAktI9coAAHmK2DcAAjL2ADMAAsTwnpkBoVgl0zdH0OgIKADYT6O0VPE3AByo4JXsMCNIzxzB+1bMZRz2U12L9cVI3o6t+3XfDD3KD98Po8tfquUrzPc8L0vowr1M6+b3ytvXeZhyo83XIEbAPgoAP3gLqTIudRgpFflLVmet6aVFqA0RWytgja2fL0ABow94dh8hXco70hz-1HBvX+VZO7d24LQscEEK7oPsjAT0epzawjgPAlA5tLZYljuJO2icHZBkRl7Ocacg4Zyeu7N6+CfZfR+gHBOdMOHAyjjHSGttrKnWADaHhTcQyB3uuyTONpEEoHzqHLCZDUzlD4V6TIgi1BBV1sHKupZqGjBvuUBh98+6DyHqVGanZD7syyj0PxLc26BLvg-J+YSuYVQgQESwcN4LJBgAAKQgDyGxgQv4gAbKg8w7DwqYMpHeFoTcVbSPQEOHuwAslQDgBAeCUBZhN1biQ2amtKG9Fae0zp3TenX2kKw1MVTAYACtCloB4QUnk7i0T7REVDAx4iKSO3wbIixj1nqvXDkVFRZk1HyCslnbxh0KHKJ0SgCGJNtl3TwmAExUzDnkXKFRW05taJhD6dIG5aMAZh0BRxbiPheJyAEjyRwIKtn6PefRbAWg3GjlhCCyZowzEaPRnyDFeobGCjkGdKZFzKX4sspDRxwJyirOWaOMuy02F3KZgzCJB9pbRM5qA7ma4qoBC8G0rsXpYDAGwD3Qg8REjIMlofKp3U+oDSGq0YwPK-xF3ZbMiFsFOHcDwIyXhxr3LaCEQdAubzwzlA4HDU1PyiWY24Ag4AOd2LXPMb8mQWN3U5yucAMFLtLF+rdTjHOULrn0p1eQiVJrLUeOpnqqCssyg9AGZEvlx8YlpKAA)
