# CucumberFramework

A **BDD (Behavior Driven Development) test automation framework** for web UI testing, built with **Java + Cucumber + Selenium + Maven**, using the **Page Object Model (POM)** design pattern.

Currently automates one flow: **user login**, tested against a public demo app at
`https://freelance-learn-automation.vercel.app/login`.

---

## 1. Tech Stack

| Tool | Purpose |
|---|---|
| Java 11 | Language |
| Maven | Build tool & dependency management |
| Cucumber (`cucumber-java`, `cucumber-junit`, `cucumber-core`) | BDD test execution — turns Gherkin scenarios into runnable tests |
| JUnit 4 | Test runner engine underneath Cucumber |
| Selenium WebDriver | Browser automation |
| ChainTest (`chaintest-cucumber-jvm`) | Test report generation (HTML dashboard + email-style report) |

---

## 2. Prerequisites

Before running this project, make sure you have:

1. **Java 11+** installed
2. **Maven 3.9+** installed
3. **Git** installed
4. **Google Chrome** installed (default browser used by the framework)

---

## 3. Project Structure

```
CucumberFramework/
├── Featurefiles/
│   └── Login.feature              # Gherkin scenario(s) — the test cases, in plain English
│
├── configuration/
│   └── config.properties          # Browser, target URL, timeouts
│
├── src/
│   ├── main/java/
│   │   ├── factory/
│   │   │   └── BrowserFactory.java    # Creates & configures the WebDriver
│   │   ├── dataprovider/
│   │   │   ├── ConfigReader.java      # Reads config.properties
│   │   │   └── DBReader.java          # (stub) placeholder for future DB validation
│   │   ├── helper/
│   │   │   └── Utility.java           # Screenshot capture helper
│   │   ├── hooks/
│   │   │   └── Hook.java              # @Before / @After / @AfterStep lifecycle logic
│   │   └── pages/
│   │       ├── LoginPage.java         # Locators + actions for the login page
│   │       ├── HomePage.java          # Locators + actions for the post-login dashboard
│   │       └── RegistrationPage.java  # (empty stub) not currently used
│   │
│   └── test/java/
│       ├── runner/
│       │   └── TestRunner.java        # Entry point — wires Cucumber to JUnit
│       └── stepDefinations/
│           └── LoginStepDefination.java  # Maps Gherkin steps to Java code
│
├── src/test/resources/
│   └── chaintest.properties       # ChainTest report configuration
│
├── pom.xml                        # Maven dependencies & build config
└── README.md
```

---

## 4. How It All Fits Together (Read This to Understand the Flow)

Think of it in **5 layers**, from "what to test" down to "how it's proven":

1. **`Login.feature`** — describes the test case in plain English (Given/When/Then). This is the source of truth for *what* is being tested.

2. **`TestRunner.java`** — the entry point. Tells Cucumber:
   - where to find feature files (`Featurefiles`)
   - where to find the matching Java code (`glue = {"stepDefinations", "hooks"}`)
   - which tests to run (`tags = "@Smoke"`)
   - which reports to generate (`plugin = {...}`)

3. **`LoginStepDefination.java`** — matches each English sentence in the feature file to a Java method (e.g. `"User enters {string} in the email field"` → `user_enters_in_the_email_field()`), and calls the relevant Page Object methods.

4. **`LoginPage.java` / `HomePage.java`** (Page Objects) — hold the actual element locators (`By.xpath(...)`) and low-level actions (type text, click button) for each web page. Step definitions never touch locators directly — they only call these methods. This separation means if the UI changes, you only update the Page Object, not the test logic.

5. **`BrowserFactory.java` + `Hook.java`** — the plumbing that runs before/after every scenario:
   - `Hook.java`'s `@Before` starts a browser session via `BrowserFactory`, using settings from `config.properties` (via `ConfigReader`).
   - `Hook.java`'s `@After` closes the browser.
   - `Hook.java`'s `@AfterStep` takes a screenshot automatically **only if the scenario failed**, and attaches it to the report.

**In short:** `Feature file → Runner → Step Definitions → Page Objects → Browser`, wrapped by Hooks that manage setup/teardown, and config in `config.properties` controls environment details (browser, URL, timeouts) without touching code.

---

## 5. Configuration

All environment settings live in `configuration/config.properties` — change these instead of hardcoding values in Java:

```properties
browser=Chrome
url=https://freelance-learn-automation.vercel.app/login
implicitWait=10
pageLoadTimeOut=60
scriptTimeOut=10
```

`ConfigReader.java` reads this file at runtime, so switching browsers or environments only requires editing this file.

---

## 6. Running the Tests

```bash
git clone https://github.com/Mukesh-50/BDDFramework2026.git
cd BDDFramework2026
mvn clean test
```

- `TestRunner.java` currently runs only scenarios tagged `@Smoke` (see `Login.feature`).
- To run everything regardless of tags, remove or change the `tags = "@Smoke"` line in `TestRunner.java`.

---

## 7. Reports

Every run generates **three types of reports** (from two independent listeners, both fed by Cucumber's test events):

| Report | Location | What it is |
|---|---|---|
| Cucumber's built-in HTML report | `target/cucumber.html` | Basic pass/fail summary, no extra styling |
| ChainTest dashboard | `target/chaintest/Index.html` | Main report — dark-themed, detailed, includes failure screenshots |
| ChainTest email report | `target/chaintest/Email.html` | Same data, formatted for pasting into an email |
| Maven/JUnit raw results | `target/surefire-reports/` | Low-level pass/fail at the JUnit class level (auto-generated by Maven, not project-specific) |

Report behavior (enabled/disabled, output paths, styling) is configured in `src/test/resources/chaintest.properties`.

👉 **After running tests, open `target/chaintest/Index.html` in a browser — that's the main report to check.**

---

## 8. Known Limitations / Notes

- `DBReader.java` is a placeholder — no database validation is implemented yet.
- `RegistrationPage.java` is an empty, unused stub — kept in case a signup flow is automated in the future (there is currently no feature file or step definitions for signup).
- Only `Chrome`, `Firefox`, and `Edge` are supported browsers (set via `config.properties`).

---

## 9. Extending This Framework

To add a new test:
1. Write a new `.feature` file under `Featurefiles/` describing the scenario in Gherkin.
2. Add a matching step definition class under `src/test/java/stepDefinations/`.
3. Add/extend a Page Object under `src/main/java/pages/` for any new page involved.
4. Tag the scenario (e.g. `@Smoke`, `@Regression`) and update `TestRunner.java`'s `tags` if needed to include it in a run.
