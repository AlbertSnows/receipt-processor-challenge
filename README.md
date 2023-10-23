# Receipt Processor

This is a project that ingests receipt data and performs calculations to determine points for a receipt.

For more info about the instructions,
go [here](./docs/Instructions.md)

The docs also contain more documentation about various design choices for this application, including the creation of a concept called [State Pairs](./docs/WhatAreStatePairs.md); a primitive rule engine.

## How to run

There are several ways to run this project. The easiest way is running `docker compose up`.
Before you can do that, you will need to make sure you have docker installed on your machine. Docker desktop comes with
compose and can be installed from their [website](https://www.docker.com/products/docker-desktop/). Otherwise, you'll
need to download the docker engine and composer plugin manually, which is explained via the documentation from their
website.

If you can navigate to
[localhost:8080/entry](localhost:8080/entry). If you get a json response, the app should be running.

For other ways to run this application, refer to the [HowToRun](./docs/HowToRun.md) docs.

## Gradle Error

If you get an error related to gradle in the form

`#10 0.310 /bin/sh: 1: ./gradlew: not found`

apparently this is a carriage return quirk between unix and windows
converting the gradle file to LF return time *should* fix the issue. 

e.g. 
https://stackoverflow.com/a/72360107
