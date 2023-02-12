# FirePowered Core Utilities

This project contains a set of utility classes shared among [FirePowered](https://FirePowered.org) Java code, such as the Discord bots.

## Contents

| Utility       | Package                            | Notes |
| ------------- | ---------------------------------- | - |
| Steam         | `org.firepowered.core.utils.steam` | |
| Http/Web      | `org.firepowered.core.utils.net`   | |
| Miscellaneous | `org.firepowered.core.utils`       | |

## Management
### Build
FirePowered uses [Maven](https://maven.apache.org/) as a build system. It is recommended to install a Maven extension into your IDE (for example, Eclipse includes [m2e](https://www.eclipse.org/m2e/)) for build and dependency support. 

This library is designed to have minimal dependencies, but some may be needed. Since this library is not a multi-module project, all dependencies are placed in the root `pom.xml`'s `<dependencies>` section.

### Source Control
The main development branch is `master`. All branches should be kept at development versions (e.g., `1.0.0-SNAPSHOT`). Smaller release versions can go on their own branch (e.g., master is at `2.0.0-SNAPSHOT` but there is still work on the `1.3` branch).

Project branches should go under `projects/`. For example, if there is a project called `rewrite`, the branch would be called `projects/rewrite`. These branches are typically large features which is why they deserve their own branch. Most normal development can occur on the branch corresponding to the release version.

Bug fixes should be done on the branch corresponding to the lowest version still in development so that branch and all future version branches get the fix as well.

## Running Tests
```
$ mvn test
```

This command will run Maven Surefire on all tests in `src/test/java`. Note that `SteamApiWrapperTest` (and some tests in `SteamIDTest`) require a Steam API key to be configured. This can be added in `src/test/resources/steamapi.properties` as

```properties
key=MY_API_KEY
```

Without that these tests will fail.