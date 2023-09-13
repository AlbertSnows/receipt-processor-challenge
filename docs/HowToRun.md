# How To Run

this project

There are four different ways to run the project that I know of.

- docker compose
- regular docker
- jar
- gradle

I'll list details about each below.

## Docker Compose

You'll need to have Docker installed on your system. Docker Compose is typically bundled with Docker, but you should
verify its availability.

Run

   ```bash
   docker-compose --version
   ```

If you have a version, great! It should just work. If not, you'll need to install it. Docker Desktop is the easiest way,
if you don't want to do it that way you'll have to refer to their [docs](https://docs.docker.com/compose/install/).

## Regular Docker

You need to build and then run the file.

Same as the previous section, make sure you have Docker installed.

```bash
docker --version
```

If not, go to their website so you can download the engine.

Once you have it installed, you need to run two commands.

```bash
docker build -t app .
```

and

```bash
docker run -p 8080:8080 app
```

That should spin the server.

## Jar

Most java libs are built on jars. You can build one for this project.

```bash
./gradlew clean build
```

The file should be `receipt-processor-0.0.1-SNAPSHOT.jar` but if you're not sure, you can check in the build/libs
directory.

Then run

```bash
java -jar build/libs/receipt-processor-0.0.1-SNAPSHOT.jar
```

and it should spin up. Oh, you'll need to have java on your system by the way. You can do that from a variety of places
online via a google search.

## gradlew

Finally, the default way during development to spin up the server is to just run

```bash
./gradlew bootRun
```