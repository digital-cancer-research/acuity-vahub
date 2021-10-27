Thank you for taking the time for contribute!  

Please see below guidelines for contributing to ACUITY project.  

## Git

### Git Workflow

* Make a feature branch from `dev` branch
* When the work is done, [rebase your commits](https://github.com/edx/edx-platform/wiki/How-to-Rebase-a-Pull-Request) ([PDF](/docs/How-to-Rebase-a-Pull-Request.pdf)) onto the latest version of the `dev` branch and check that the new code works as expected and there are no conflicts
* Create a pull request to `dev` branch 

### Pull request checklist

Before submitting the pull request make sure:

* The issue/feature has been dev-tested
* All the new code is covered with unit tests
* All tests pass and production bundle can be built
```
mvn clean package -P webapp,checks
```

### Naming conventions
Commit message should be written in the imperative mood, starting with an uppercase letter. For example:
```
Fix SSV console error when switching to timeline tab
```
Commit message should tell you "Why?" while code responds on "How?"

## Code style

**Java**  

Coding standards for Java are checked by [CheckStyle](https://checkstyle.sourceforge.io/) tool.  
CheckStyle configuration file is `/vahub-config/src/main/resources/checkstyle.xml`.
See [ACUITY Acuity Wiki](TODO:add link) on how to configure CheckStyle plugin for the IDE you're using.  

**JavaScript**  

Coding standards for TypeScript are checked by [TSLint](https://palantir.github.io/tslint/). Configure TSLint check with Git pre-commit hook:

1. Copy `/vahub-config/src/main/resources/pre_commit` to `.git/hooks`
2. Run the following command from `/vahub/src/main/webapp`:
    ```
    npm run prehooks-install
    ```
<!--
## Miscellaneous

### How to update application version
Run
```
mvn versions:set -DnewVersion=1.1-SNAPSHOT -DprocessAllModules
```
If everyting is OK, then
```
mvn versions:commit -DprocessAllModules
```
-->
