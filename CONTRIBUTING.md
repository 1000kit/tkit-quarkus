# Contributing guide

**Want to contribute? Great!**
We try to make it easy, and all contributions, even the smaller ones, are more than welcome.
This includes bug reports, fixes, documentation, examples...
But first, read this page (including the small print at the end).

## Legal

All original contributions to 1000kit Quarkus are licensed under the
[ASL - Apache License](https://www.apache.org/licenses/LICENSE-2.0),
version 2.0 or later, or, if another license is specified as governing the file or directory being
modified, such other license.

All contributions are subject to the [Developer Certificate of Origin (DCO)](https://developercertificate.org/).
The DCO text is also included verbatim in the [dco.txt](dco.txt) file in the root directory of the repository.

## Reporting an issue

This project uses Gitlab issues to manage the issues. Open an issue directly in Gitlab.

If you believe you found a bug, and it's likely possible, please indicate a way to reproduce it, what you are seeing and what you would expect to see.
Don't forget to indicate your Quarkus, Java, Maven/Gradle and GraalVM version. 

### Building main

Just do the following:

```
git clone git@gitlab.com:1000kit/libs/quarkus/tkit-quarkus.git
cd tkit-quarkus
export MAVEN_OPTS="-Xmx4g"
mvn -B impsort:check formatter:validate verify --file pom.xml
```
or native build and tests
```
mvn -B -Pnative impsort:check formatter:validate verify --file pom.xml
```

Wait for a bit and you're done.

**Note** For Apple Silicon computer, Rosetta must be installed. It can be done using `softwareupdate --install-rosetta`

### Updating the version

When using the `main` branch, you need to use the group id `org.tkit.quarkus` and version `999-SNAPSHOT` of the 1000kit Quarkus BOM.

You can now test your application.

## Before you contribute

To contribute, use Gitlab Pull Requests, from your **own** branch `feat/<name-of-feature>`.

Also, make sure you have set up your Git authorship correctly:

```
git config --global user.name "Your Full Name"
git config --global user.email your.email@example.com
```

If you use different computers to contribute, please make sure the name is the same on all your computers.

We use this information to acknowledge your contributions in release announcements.

### Code reviews

All submissions, including submissions by project members, need to be reviewed by at least one Quarkus committer before being merged.

[GitHub Pull Request Review Process](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/reviewing-changes-in-pull-requests/about-pull-request-reviews) is followed for every pull request.

### Coding Guidelines

* We decided to disallow `@author` tags in the Javadoc: they are hard to maintain, especially in a very active project, and we use the Git history to track authorship. Gitlab also has [this nice page with your contributions](https://gitlab.com/1000kit/libs/quarkus/tkit-quarkus/-/graphs/main). For each major 1000kit Quarkus extensions release, we also publish the list of contributors in the announcement post.
* Commits should be atomic and semantic. Please properly squash your pull requests before submitting them. Fix-up commits can be used temporarily during the review process but things should be squashed at the end to have meaningful commits.
  We use merge commits so the Gitlab Merge button cannot do that for us. If you don't know how to do that, just ask in your pull request, we will be happy to help!

### Continuous Integration

Because we are all humans, and to ensure 1000kit Quarkus extension is stable for everyone, all changes must go through 1000kit Quarkus extension continuous integration. 1000kit Quarkus extension CI is based on Gitlab pipelines, which means that everyone has the ability to automatically execute CI in their forks as part of the process of making changes. We ask that all non-trivial changes go through this process, so that the contributor gets immediate feedback, while at the same time keeping our CI fast and healthy for everyone.

### Tests and documentation are not optional

Don't forget to include tests in your pull requests.
Also don't forget the documentation (reference documentation, javadoc...).

Be sure to test your pull request in:

1. Java mode `mvn -B impsort:check formatter:validate verify --file pom.xml`
2. Native mode `mvn -B -Pnative impsort:check formatter:validate verify --file pom.xml`
