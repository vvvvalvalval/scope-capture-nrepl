# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

## 0.3.1 - 2019-01-25
- in-ep checks for existence of the Execution Point.
- requires scope-capture 0.3.2

## 0.3.0 - 2017-10-08
### Added
- Support for newer nrepl 0.5.3 while preserving compatibility with tools.nrepl.

### Changed
- moved nrepl deps to provided profile - users must now explicitly state their nrepl dependency.

## 0.2.0 - 2017-10-08
### Added
- For convenience, `sc.nrepl.repl` is now required by `sc.nrepl.middleware`

## 0.1.0 - 2017-10-08
### Added
- Files from the new template.
- nREPL Middleware
- REPL functions: `in-ep`, `exit`
- README
- basic example tests

[Unreleased]: https://github.com/your-name/scope-capture-nrepl/compare/0.3.1...HEAD
