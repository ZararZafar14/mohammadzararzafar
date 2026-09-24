# Banker's Algorithm Android App

A Java Android simulator for the standard Banker's Algorithm for deadlock avoidance.

## Features
- Available resource vector input
- Allocation matrix input
- Maximum matrix input
- Automatic Need calculation (Maximum - Allocation)
- Input validation
- Safe/unsafe state detection
- Safe sequence generation
- Blocked-process reporting when unsafe
- Classic sample loader
- SQLite history for analyses
- Clear/view saved history
- No external libraries

## Input format
Available: `3 3 2`

Allocation rows are separated by semicolons:
`0 1 0; 2 0 0; 3 0 2; 2 1 1; 0 0 2`

Maximum:
`7 5 3; 3 2 2; 9 0 2; 2 2 2; 4 3 3`

Need is calculated automatically. A process can finish when Need <= current Work. After it finishes, its Allocation is returned to Work.

## Run
Open `BankersAlgorithmApp` in Android Studio, sync Gradle, and run the app module.
