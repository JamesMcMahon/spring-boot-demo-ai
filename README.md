# Spring AI Demos

The repository has two independent examples built with Spring Boot:

| Demo                    | What it illustrates                                                                     | Package    | Note                                                                                                                            |
|-------------------------|-----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------------------------------------------|
| **Date/Time Tools**     | Simple local tool calling                                                               | `datetime` |                                                                                                                                 |
| **Cat Adoption Agency** | • Chat memory<br>• RAG with pgvector / PostgresML<br>• Tool calling<br>• System Prompts | `adoption` | Adapted from the blog post  “[Your First Spring AI 1.0 Application](https://spring.io/blog/2025/05/20/your-first-spring-ai-1)”. |

## Prerequisites

* JDK 23+
* Docker & Docker Compose
* OpenAI API key exported as `SPRING_AI_OPENAI_API_KEY`. *You could potentially set up another API by supplying another
  key and changing the model starter in `pom.xml`.*

## Running locally

Spring Boot’s built-in **Docker Compose** support automatically
launches the services defined in `compose.yaml`.

Simply run:

```bash
./mvnw spring-boot:run
```

## Try it out

### Date/Time Tools demo

```bash
curl -X POST "http://localhost:8080/datetime" \
     -H "Content-Type: application/json" \
     -d '{"prompt":"What is the current time in Tokyo?"}'
```

```bash
xh post http://localhost:8080/datetime prompt="What is the current time in Tokyo?"
```

### Cat Adoption chat

```bash
# Ask about available cats
curl "http://localhost:8080/alice/assistant?question=Do%20you%20have%20any%20playful%20cats%3F"
```

```bash
xh get "http://localhost:8080/alice/assistant" question=="Do you have any playful cats?"
```

### Schedule a pickup

```bash
curl "http://localhost:8080/alice/assistant?question=Great%21%20Can%20I%20schedule%20to%20pick%20up%20Luna%20tomorrow%3F"
```

```bash
xh get "http://localhost:8080/alice/assistant" question=="Great! Can I schedule to pick up Luna tomorrow?"
```

## Credit where credit is due

The majority of this code is adapted
from [Your First Spring AI 1.0 Application blog post](https://spring.io/blog/2025/05/20/your-first-spring-ai-1).

Josh Long has his own version of this code and various talks on the "Dog Adoption" use case.

- https://github.com/joshlong-attic/2025-05-13-goto-state-of-the-art-series/
- https://www.youtube.com/watch?v=9mOuvrZtLbc

What this repo adds, aside from changing dogs to cats, is another simple tool example, comments around the code, and a
full working Docker setup so you can easily boot and run the example code locally. The code is mostly a from-scratch
re-implementation of the blog post. Some structural changes were made to make it easier to follow.