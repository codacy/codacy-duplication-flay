# Codacy Duplication Flay

This is the duplication docker we use at Codacy to have [Flay](https://github.com/seattlerb/flay) support.
You can also create a docker to integrate the tool and language of your choice!
Check the **Docs** section for more information.

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/85242dbf532742889a0b856ee11d022a)](https://www.codacy.com/gh/codacy/codacy-duplication-flay?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=codacy/codacy-duplication-flay&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/85242dbf532742889a0b856ee11d022a)](https://www.codacy.com/gh/codacy/codacy-duplication-flay?utm_source=github.com&utm_medium=referral&utm_content=codacy/codacy-duplication-flay&utm_campaign=Badge_Coverage)
[![Build Status](https://circleci.com/gh/codacy/codacy-duplication-flay.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/codacy/codacy-duplication-flay)

## Usage

> TODO

## Docs

> TODO

## Agent Playbook: Updating This Repository End-to-End

This section is written for an AI coding agent (or a human) tasked with updating this repo — most commonly bumping the wrapped [Flay](https://github.com/seattlerb/flay) version, but also base image / orb / Scala-seed dependency bumps. Follow it top to bottom.

### 1. What this repository is

This is a Codacy **DUPLICATION engine** (not a pattern-based linting engine): a thin Scala wrapper (`src/main/scala/codacy/duplication/flay/Flay.scala`, built on `codacy-duplication-scala-seed`) that shells out to [Flay](https://github.com/seattlerb/flay), a Ruby duplicate-code detector, and translates its output into Codacy's `DuplicationClone`/`DuplicationCloneFile` model (see `FlayReport.scala` for the JSON shape Flay is expected to emit and `Flay.scala` for the mapping).

Unlike a typical Codacy tool wrapper, **Flay itself is vendored source, not a Ruby gem dependency**: the actual tool code lives at `src/main/resources/flay/` (`bin/flay`, `lib/flay.rb`, `lib/flay_erb.rb`, `lib/flay_task.rb`, `lib/gauntlet_flay.rb`, `Rakefile`, `History.txt`, `Manifest.txt`, `README.txt`) and is copied verbatim from an upstream Flay release. `build.sbt` maps these files (plus `Gemfile`, `Gemfile.lock`, `.ruby-version`) into the Docker image, and `Common.scala` sets `ENV FLAY_SOURCE_PATH /opt/codacy/flay`. At runtime, `Flay.scala` runs `rake codacy[<dir>]` inside that directory (see the `:codacy` task in the vendored `Rakefile`), which invokes vendored `bin/flay --diff --report` and prints a single line of JSON to stdout — that JSON is parsed by `FlayReport.scala` into Codacy's duplication contract. The Ruby gems in the top-level `Gemfile`/`Gemfile.lock` (`rake`, `hoe`, `sexp_processor`, `ruby_parser`, `ruby2ruby`, `erubis`, `minitest`) are Flay's own *runtime dependencies*, installed via `bundle install` in the Docker image — they are not Flay itself.

There is **no `docs/patterns.json`** in this repo (confirmed absent) and no rule/pattern list of any kind — duplication tools don't have configurable patterns. The only "docs" config that exists is a small fixture set used by `codacy-plugins-test`'s duplication test mode:

- `src/main/resources/docs/duplication-tests/contain-results/src/*.rb` — sample Ruby source fed to the engine.
- `src/main/resources/docs/duplication-tests/contain-results/results.xml` — the expected duplication report for that sample, in the `codacy-plugins-test` XML contract (`<checkstyle version="4.3"><duplication nrTokens=".." nrLines=".." message="..">...`).
- `src/main/resources/docs/duplication-tests/no-results/` — same idea, but a sample with no duplicates (`results.xml` is an empty `<checkstyle>` element).

These fixture directories are mapped into the Docker image under `/docs` (see `build.sbt`'s `mappings.in(Universal)`), which is where `codacy-plugins-test` reads them from. There is also `src/test/scala/codacy/duplication/flay/FlaySpec.scala`, a native specs2 test that runs `Flay(...)` against `src/test/resources/analysis/duplication/ruby/*.rb` and asserts the exact `DuplicationClone`s produced.

### 2. Files that encode versions — check all of these on every update

| File | What it controls | What to check |
|---|---|---|
| `src/main/resources/flay/lib/flay.rb` (`VERSION = "..."`) and the other vendored files under `src/main/resources/flay/` | The actual Flay release bundled and executed by this engine | This is the file that matters most. Replace the vendored files with the contents of the target `seattlerb/flay` release tag (e.g. from its gem package or GitHub tag), not just the `VERSION` string — bin/flay, lib/*, Rakefile, History.txt, Manifest.txt, README.txt should all come from the same release. **Caution:** a prior commit (`f5d56f0`, "Bump Flay 2.13.3") only updated the *test-fixture copy* of `flay.rb` under `src/main/resources/docs/duplication-tests/contain-results/src/` and left the real vendored `src/main/resources/flay/lib/flay.rb` at `2.7.0` — verify which file(s) you're actually changing. |
| `Gemfile` / `Gemfile.lock` | Flay's own Ruby dependency versions (`sexp_processor`, `ruby_parser`, `ruby2ruby`, `erubis`, `rake`, `hoe`, `minitest`) | Check the target Flay release's own `.gemspec`/`Rakefile` `dependency` declarations for required version ranges, and regenerate `Gemfile.lock` with `bundle install`/`bundle lock` under the matching Ruby version. |
| `.ruby-version` | Ruby version used to build/run bundle install and by the Alpine `apk add ruby*` packages in `Common.scala` | Bump if the target Flay release needs a newer Ruby, or as part of routine Ruby maintenance. |
| `project/Dependencies.scala` → `Codacy.duplicationSeed` | `codacy-duplication-scala-seed` version (the Codacy engine API contract this wrapper implements) | Bump to the latest published version; check for breaking API changes in `DuplicationTool`/`DuplicationClone`. |
| `project/Common.scala` → `dockerBaseImage` | Runtime base image (`amazoncorretto:8-alpine3.22-jre`) and the `apk add` package list for Ruby | Bump the Alpine tag for security patches; keep the `apk add` ruby package list in sync with what Flay's Rakefile/gems actually need. |
| `.circleci/config.yml` → `codacy/base` and `codacy/plugins-test` orbs | Shared CircleCI steps and `codacy-plugins-test` runner | Check the latest published orb versions. |
| `build.sbt` → `semanticdbVersion` / `project/plugins.sbt` sbt plugin versions | Build tooling only, no runtime effect | Bump opportunistically alongside other changes; not required for a pure Flay bump. |

Note: there is no `Dockerfile` in this repo — the Docker image is built entirely via `sbt-native-packager`'s `DockerPlugin`, configured in `build.sbt` and `project/Common.scala` (`dockerCommands`, `dockerBaseImage`, etc.).

### 3. Step-by-step update procedure

1. **Identify the target Flay version** and fetch its source (e.g. `gem fetch flay -v <version>` and unpack, or clone `seattlerb/flay` at tag `v<version>`).
2. **Replace the vendored files** under `src/main/resources/flay/` with the new release's `bin/flay`, `lib/flay.rb`, `lib/flay_erb.rb`, `lib/flay_task.rb`, `lib/gauntlet_flay.rb`, `Rakefile`, `History.txt`, `Manifest.txt`, `README.txt`. Keep the existing `:codacy` rake task in the `Rakefile` (it's Codacy-specific, not part of upstream Flay) — merge it into the new `Rakefile` rather than dropping it.
3. **Update `Gemfile`/`Gemfile.lock`** to match the new release's dependency constraints, and `.ruby-version` if required. Run `bundle install` (or `bundle lock`) locally with the target Ruby to regenerate `Gemfile.lock` deterministically.
4. **Re-run the native test suite**: `sbt test` (runs `FlaySpec`). If Flay's clone-detection output format or exact duplicate findings on the fixtures under `src/test/resources/analysis/duplication/ruby/` changed between versions, update the expected `DuplicationClone`s in `FlaySpec.scala` to match.
5. **Check the `codacy-plugins-test` fixtures**: if the new Flay version changes the JSON/duplication output for the sample files under `src/main/resources/docs/duplication-tests/*/src/`, regenerate `results.xml` in each fixture directory (`contain-results/results.xml`, `no-results/results.xml`) to match the new expected output.
6. **Build and format**: `sbt scalafmtCheck scalafmtSbtCheck scapegoat` (same as the CI `lint` job), fixing with `sbt scalafmt scalafmtSbt` where needed.
7. **Build the Docker image locally**: `sbt "set version in Docker := \"latest\"; docker:publishLocal"`.
8. **Run `codacy-plugins-test` locally** before pushing — clone https://github.com/codacy/codacy-plugins-test and run its duplication-mode `DockerTest` command against your local image tag (mirrors the CI `plugins_test` job, which runs with `run_duplication_tests: true` and all other test flags `false`).
9. **Iterate on failures**, re-running only the relevant command after each fix.
10. **Commit** the version bump(s) together with any regenerated fixtures/lockfiles in one change.
11. **Push and open a PR.**
12. **Poll the PR's real CI checks until they all pass — local validation is NOT the finish line.** After every push, run `gh pr checks <pr-url>` and keep re-polling (short sleep while any check is `pending`) until all checks finish. If a check fails, fetch its actual log (don't guess), find the true root cause, fix it, push again (never `--no-verify`, never force-push), and re-poll. Repeat until every check is green. **The CI environment's toolchain can differ from your local one**, so a clean local run does not guarantee CI passes. Only stop iterating when every check passes, or you hit a genuine product/infra decision that needs a human.

### 4. Common failure modes and fixes

| Symptom | Likely cause | Fix |
|---|---|---|
| `FlaySpec` fails with different clone counts/line numbers than expected | The new Flay version changed its structural-hash/mass algorithm or output format | Update the expected `DuplicationClone` list in `FlaySpec.scala` to the new (correct) output — don't just force the old values. |
| `plugins_test` duplication job fails on `contain-results`/`no-results` | `results.xml` fixtures are stale relative to the new Flay output | Regenerate `results.xml` by running the engine against the fixture `src/` files and capturing real output. |
| Docker build fails installing gems (`bundle install`) | `.ruby-version` / Alpine `apk add ruby*` packages out of sync with `Gemfile.lock`'s `BUNDLED WITH` version | Align the Ruby/Bundler version across `.ruby-version`, `Gemfile.lock`, and the `apk add` package list in `Common.scala`. |
| Engine returns "No output" / "Output should contain a single line" errors at runtime | Vendored `Rakefile`'s `:codacy` task got dropped or altered while updating from upstream, or `bin/flay --diff --report` no longer emits single-line JSON | Confirm the `:codacy` rake task still exists and still runs `bin/flay --diff --report` per the current `Rakefile`; check upstream Flay's CLI output changes. |

### 5. Definition of done

- The vendored `src/main/resources/flay/` files (not just a fixture copy) reflect the target Flay version.
- `Gemfile`/`Gemfile.lock`/`.ruby-version` are consistent with the new Flay release's dependencies.
- `sbt test` (native `FlaySpec`) passes, with expectations updated if Flay's output changed.
- `sbt scalafmtCheck scalafmtSbtCheck scapegoat` passes.
- Docker image builds successfully via `sbt docker:publishLocal`.
- `codacy-plugins-test` duplication-mode tests pass locally against the freshly built image, with `results.xml` fixtures regenerated if needed.
- **After pushing and opening/updating the PR, every CI check on it is green.** Poll `gh pr checks <pr-url>` and iterate on any failure until all pass.

## Test

> TODO

## What is Codacy

[Codacy](https://www.codacy.com/) is an Automated Code Review Tool that monitors your technical debt, helps you improve your code quality, teaches best practices to your developers, and helps you save time in Code Reviews.

### Among Codacy’s features

- Identify new Static Analysis issues
- Commit and Pull Request Analysis with GitHub, BitBucket/Stash, GitLab (and also direct git repositories)
- Auto-comments on Commits and Pull Requests
- Integrations with Slack, HipChat, Jira, YouTrack
- Track issues in Code Style, Security, Error Proneness, Performance, Unused Code and other categories

Codacy also helps keep track of Code Coverage, Code Duplication, and Code Complexity.

Codacy supports PHP, Python, Ruby, Java, JavaScript, and Scala, among others.

### Free for Open Source

Codacy is free for Open Source projects.
