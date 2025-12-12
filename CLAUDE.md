# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Advent of Code 2025 repository using Kotlin. The project follows a day-by-day structure where each day's puzzle has its own class.

## Project Structure

- `src/` - Contains solution classes (e.g., `Day1.kt`, `Day2.kt`, etc.)
- `test/` - Contains test files (not yet created)
- `resources/` - Contains input data files (not yet created)
- `testResources/` - Contains test input data files (not yet created)

## Development Setup

This is an IntelliJ IDEA project using:
- Kotlin 2.2 (API and language version)
- JVM target 1.8
- Kotlin standard library (KotlinJavaRuntime)

## Code Architecture

Each day's puzzle solution follows this pattern:
- A class named `DayN` (where N is the day number)
- Two methods: `solvePart1(input: List<String>): Int` and `solvePart2(input: List<String>): Int`
- Input is provided as a list of strings (one per line from the puzzle input file)

## Running Code

Since this is a standard IntelliJ IDEA Kotlin project without a build tool (Gradle/Maven), you'll need to:
1. Compile and run directly from IntelliJ IDEA
2. Or use `kotlinc` command line tools to compile manually:
   - Compile: `kotlinc src/DayN.kt -include-runtime -d DayN.jar`
   - Run: `kotlin -classpath DayN.jar DayNKt`

## Testing

Test files should be placed in the `test/` directory and test resources in `testResources/` directory. The project uses IntelliJ's test framework support.
