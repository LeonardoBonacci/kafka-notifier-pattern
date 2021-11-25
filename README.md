# Example application of the Kafka notifier-pattern

This project demonstrates how Apache Kafka can be used in combination with a/any data storage solution in a non-blocking streaming fashion.
Only requirement is the availability of a reactive driver.

In a simple, generic and flexible manner, it uses reactive messaging for non-blocking processing, on both the sink and the source.
Within a demo Kafka topology, this project uses Reactive Quarkus applications as processors that leverage Neo4j for a non-trivial graph-computation.

## Blog post

Further detail and analysis is provided in this-TODO blog post.

## Use-case

- Given a stream/topic of products..
- ..and a stream/topic of hierarchy nodes
    - The hierarchy nodes together form an inverted tree (used i.e. for bread crumbs) 
    - All nodes that form a latest version of a complete hierarchy tree all stream through 'at the same time' i.e. once a day
- Output is a product with a path of hierarchy nodes until the root
    - The product with its hierarchy is represented as a nested data structure (product with parent hierarchy node with parent hierarchy node, etc.)
    - The product only flows out if there exists a path until the root 
- The height of the tree is unknown
- A product upsert - with a path to the root - flows out as a product hierarchy entity
- A hierarchy node upsert flows out product hierarchy entities for all products - with a path to the root - in its subtree 

TODO diagram

## Start the broker

You need a Kafka broker and a Neo4j database.

```
docker-compose up -d
```

## Start the applications

To run your applications.

```
mvn quarkus:dev (3 times)
```

## Use the application

Execute the following curl-command to receive the results as server side events.

```
curl -N localhost:8082/producthierarchies

{
  "id": "foo",
  "label": "v1",
  "parentId": "n1",
  "parent": {
    "id": "n1",
    "label": "v1",
    "parentId": "root",
    "parent": {
      "id": "root",
      "label": "v1",
      "parentId": null
    }
  }
}

```

Execute the following curl-commands in any order as input.

```
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"id":"root", "label":"v1"}' \
  http://localhost:8080/hierarchies

curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"id":"n1", "label":"v1", "parentId":"root"}' \
  http://localhost:8080/hierarchies

curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"id":"n2", "label":"v1", "parentId":"root"}' \
  http://localhost:8080/hierarchies

curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"id":"foo", "label":"v1", "parentId":"n1"}' \
  http://localhost:8081/products

curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"id":"bar", "label":"v1", "parentId":"n2"}' \
  http://localhost:8081/products
```

```
Open http://localhost:7474 and execute 'MATCH (n) RETURN n' to see the data structure.
```

## Further reading

- https://smallrye.io/smallrye-reactive-messaging/smallrye-reactive-messaging/3.1/kafka/kafka.html
