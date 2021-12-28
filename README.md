# Background
Using the actor model (akka-http) this project implements a simple realtime bidding platform based on [these requirements](https://docs.google.com/document/d/1lS7AQ0yH6hRQY8_Usfy9mjWJFh7OW4dFst0dFO0GhXs/edit)

Read about how rtb platforms work here - https://en.wikipedia.org/wiki/Real-time_bidding

In this project, the matching between campaigns and bids is done (in order) as follows:
1. Country of the campaign and targeted site IDs match the bid's

2. The price set for the campaign is greater than or equal to the bid's floor

3. The banner(s) in the campaign have a valid width and height i.e. width and height between the bid's min and max allowances

# Usage
This project uses ```Scala 2.13.4``` and ```sbt 1.5.8```

``` 
$ git clone https://github.com/kelvingakuo/eskimi-realtime-bidding

$ cd realtimebidding
```
## Start server
```sbt run```

Starts a server on ```localhost:8080```

## Testing
### Unit tests
```sbt test```

### HTTP

Modify the file ```realtimebidding/src/main/scala/Campaign.scala``` to have different campaign configurations.

Use ```cURL``` or any REST client to pass the bid request to the server. See the file ```test_rtb.http``` for a sample request

# Scala file contents
In the directory ```realtimebidding/src/main/scala```, the files are:

1. ```Main.scala``` - Entry point of the server. Defines the routes, binds to the host etc.

2. ```Protocol.scala``` - Class definitions for the different elements of rtb i.e. campaign, impression etc.

3. ```Campaingn.scala``` - Sample campaigns in form of a *Seq* of *Campaign* objects

4. ```BiddingActor.scala``` - Defines the bidding logic as an Actor.
