# RestNote

## System Requirements

Java 8 or higher binary in `PATH`.

`JAVA_HOME` must be set for the Gradle Wrapper to function.

## Build and Run

Navigate to the root of the checked out repository then run the following:

```bash
./gradlew build
java -jar build/libs/rest-note-0.1.0.jar
```

This will build, run unit tests, and launch the application which by default listens on port 80. To change the port edit the `server.port` in `src/main/resources/application.properties`.

## API

The notes API lives at `/api/notes`.

### Creating a note

When posting to the `/api/notes` route, a new note will be created.

Example:

```shell
curl -i -H "Content-Type: application/json" -X POST -d 'Be sure to drink your Ovaltine!' "http://localhost/api/notes"
```

Returns:

```json
{
  "id" : 2,
  "body" : "Be sure to drink your Ovaltine!"
}
```

### Getting an existing note

When getting from the `/api/notes/{id}` route, the requested note will be returned.

Example:

```shell
curl -i -H "Content-Type: application/json" -X GET "http://localhost/api/notes/1"
```

Returns:

```json
{
  "id" : 1,
  "body" : "Gift idea: Red Rider BB gun"
}
```

### Getting all existing notes

When getting from the `/api/notes` route, all notes will be returned.

Example:

```shell
curl -i -H "Content-Type: application/json" -X GET "http://localhost/api/notes"
```

Returns:

```json
[
  {
    "id" : 1,
    "body" : "Gift idea: Red Rider BB gun"
  },
  {
    "id" : 2,
    "body" : "Be sure to drink your Ovaltine!"
  }
]
```

### Searching notes

There are two query parameters involved in searching notes, `query` and `matchAny`. Query specifies one or more words to search for. By default, only notes that contain **all** of the words will be returned. By passing in `matchAny=true` however, notes that contain **any** of the words will be returned. The search is case and punctuation insensitive.

Example:

```shell
curl -i -H "Content-Type: application/json" -X GET "http://localhost/api/notes?query=ovaltine"
```

Returns:

```json
[
  {
    "id" : 2,
    "body" : "Be sure to drink your Ovaltine!"
  }
]
```

Example:

```shell
curl -i -H "Content-Type: application/json" -X GET "http://localhost/api/notes?query=ovaltine%20gift"
```

Returns:

```json
[]
```

Example:

```shell
curl -i -H "Content-Type: application/json" -X GET "http://localhost/api/notes?query=ovaltine%20gift&matchAny=true"
```

Returns:

```json
[
  {
    "id" : 1,
    "body" : "Gift idea: Red Rider BB gun"
  },
  {
    "id" : 2,
    "body" : "Be sure to drink your Ovaltine!"
  }
]
```

## Test notes

To prepopulate the application with some sample notes, execute the script at `test/populateNotes.sh`. This will post 100 random slogans from the adjacent `slogans.txt` file for easy end-user testing.

