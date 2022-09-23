# Server-Client Socket

This is a project for the class *Computer Security* at Maastricht University.

## Behaviour
The server allows the client to submit an id and password. if that same combination of id and pass
are already connected, the client may have access to the same resources (count). The server will respond with a json
saying if login was successful. Now that the client has logged in, it may now send actions to be performed on the server
(`INCREASE`, `DECREASE`). The server will log the counter and actions performed. All data will be lost as soon as the client
disconnects (except for the logs).

## System specifications

* The client can send messages to the server. They are read in the form of **json objects**.
* The server is able to handle multiple clients at a time by handling each client on a different thread.
* Server logs are automatically written to `resources/logs/log.txt`. An example can be found in the same dir.

## Setup
* Be sure you are running a recent version of Java (18) end Gradle (7.4).
* Unzip the file and open it in your favourite IDE.
* Run `./gradlew build` in your terminal.

## Run
Example of how to run the **Client** socket:
```
public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        Client client = new Client("config/configuration.json"); // files are found inside the "resources" dir.
        Thread clientThread = new Thread(client1); // we run the client as a thread so we can run multiple at a time.
        clientThread.start();
}
```

To run the **Server** socket:
```
public static void main(String[] args) throws IOException {
    Server server = new Server(1234); // pass in the port
    server.start();
}
```


## Dependencies
* [JSON In java](https://mvnrepository.com/artifact/org.json/json)