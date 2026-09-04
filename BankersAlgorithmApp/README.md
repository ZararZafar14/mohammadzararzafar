# Banker's Algorithm Android App

A simple Java Android simulator for learning the Banker's Algorithm and deadlock avoidance.

## Features
- Clean Android UI
- Enter process/resource need vectors
- Checks whether the system can reach a safe state
- Displays a safe execution sequence when one exists
- No database and no external libraries required

## Run
Open the `BankersAlgorithmApp` folder in Android Studio and let Gradle sync. Then run the `app` module on an emulator or Android device.

## Input
Enter rows separated by semicolons. Values in each row are separated by spaces.

Example:
`3 3 2; 1 2 2; 2 1 1`

The sample simulator starts with 3 available units of each resource. This project is intended as an educational demonstration.
