# json-diff-microservice
Spring Boot microservice with Mongo DB and Generic LRU Cache implementation

A High performance REST API that shows the differences between left and right of a root.


Clone the repository

```bash
git clone https://github.com/ogz00/json-diff-microservice.git
```

## Manual Installation

Prerequisites For manual installation:

- OpenJDK 1.11 
- Maven 3.x
- MongoDB 4.x

Before the run spring-boot application, MongoDB should be on running state and you may want to make some changes on **application.properties** file.
 
```bash
    cd ~/json-diff-microservice
    mvn install
    java -jar target/scalable-web-0.1-SNAPSHOT.jar
```
Also you can use spring-boot maven plugin with mvn spring:boot run

## Auto Installation

For sake of micro-services , docker-compose highly recommended

Build and Run the application with:

```bash
docker-compose up
```

## Usage

This application developed for calculate differences between right and left side of given root id. Left and right nodes are keeps JSON based content that given from post requests.

According to my assumption this system will have write heavy usage for providing new content. And because of the heavy writes, system should avoid from the update process and should keep history of write requests according to their timestamp and never update any of document. All of implementation had made for this two requirements.
For this use-case scenario, MongoDB had been selected as a main storage system. 


**Provide Data API**, developed for adding new content to desired root

```
curl -X POST \
  http://localhost:8080/v1/diff/{root}/left \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"content": "abcabc12="
}'
  ```
If requested id exists at the system find last version of that content and update it's data iteratively. Else create new content with desired id and persist this value for both **LRUCache** and **MongoDB**.

This api provides current value of the content like this:
```
{
    "uuid": "a8676d11-ef5b-48fa-bee5-5b6a825a7ea3",
    "rootId": "${root}",
    "target": "LEFT",
    "data": "abcabc12="
} 
```

With this approach I handle two things. Firstly I generate and audit logic for use-case history of the API and create unique UUID for every transactions. Secondly as I mention before I increase the write performance off application.

Also I implement LRU Cache mechanism for decrease the database requests. Detailed explanation are already written at the class files.


**Show Results API**, developed for check the current situation of given root.
```
curl -X POST \
  http://localhost:8080/v1/diff/{root} \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{}'
 ```

Generates results for current situation according to scenario and keep current situation at the cache. In case of any updates happened at one of the child contents, this result will be evicted from the cache.

This requests may produce three different result, according the situation of left and right contents.

```    
    EQUAL,
    NOT_EQUAL,
    SIZE_NOT_MATCH 
```
If contents of children's are in same size but not equal as a binary string returns:

```{
    "id": "${root}",
    "comparisonResult": "NOT_EQUAL",
    "details": [
        {
            "offset": 1,
            "length": 1
        },
        {
            "offset": 5,
            "length": 2
        },
        {
            "offset": 21,
            "length": 2
        }
    ]
}  
```

If contents of children's not of equal in size, returns:
```
{
    "id": "${root}",
    "comparisonResult": "SIZE_NOT_MATCH"
}
```

If contents of children's equal in both of size and data, returns:
```
{
    "id": "${root}",
    "comparisonResult": "EQUAL"
}
```
## Hashing and Cache Implementation
MD5 Hashing algorithm used for generate cache key with combination of root id and target child value. With this implementation, probability of duplication on cache has minimized.

For cache, LRU Cache approach has used and implemented with LinkedList logic and HashMap Data-Structure. Because of the current implementation just for conceptual design, Cache developed to be capable of holding different types of object with Java Generic implementation.

## Testing
Spring-Rest and Service tests are implemented appropriately.

## License
[MIT](https://choosealicense.com/licenses/mit/)
