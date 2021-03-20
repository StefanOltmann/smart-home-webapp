# Stefans Smart Home WebApp

**PROTOYPE**

## Prerequisites

You need a running instance of my [smart home server](https://github.com/StefanOltmann/smart-home-server).

Also you need a machine that has an Java JRE 11 installed.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew clean bootRun
```

## Packaging the application

The application can be packaged using:

```shell script
./gradlew clean build -Pvaadin.productionMode
```

It produces the `smart-home-webapp-XY.jar` file in the `/build/libs` directory.

## Using the server

You need to put some files aside the `smart-home-webapp-XY.jar`:

- A file named `server_url.txt` that contains the URL of the API. For example `https://192.168.0.42:50000/`.
- A filed named `auth_code.txt` that gets generated by starting the smart home server for the first time.

You can run the server using `java -jar smart-home-webapp-XY.jar`.

You can reach the service on [http://localhost:50500](http://localhost:50500).

For now the credentials are `user` and `password`.