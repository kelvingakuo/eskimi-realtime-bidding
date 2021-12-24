package com.rtbkg;

import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import spray.json.DefaultJsonProtocol._

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }
import scala.io.StdIn

import com.rtbkg.BiddingActor;
import com.rtbkg.{Campaign, Banner, Impression, BidRequest};

object BiddingAgent {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[BiddingAgent.Response] = ActorSystem(BiddingActor.apply, "bidding-agent");
    implicit val executionContext = system.executionContext;
    val bidder: ActorRef[BiddingAgent.Response] = system;
    implicit val bidReqFormat = jsonFormat5(BidRequest);

    val route: Route =
      path("bid") {
        post {
          entity(as[BidRequest]) { req => 
            bidder.
          }
        }
      }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route);

    println(s"Server now online at http://localhost:8080/ \nPress RETURN to stop...");
    StdIn.readLine(); // Stop running by pressing ENTER
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}