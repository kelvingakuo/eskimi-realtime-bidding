package com.rtbkg;

import akka.actor.typed.{ActorRef, ActorSystem};
import akka.actor.typed.scaladsl.AskPattern._;

import akka.http.scaladsl.Http;
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._;
import akka.http.scaladsl.model.StatusCodes;
import akka.http.scaladsl.server.Directives._;
import akka.http.scaladsl.server.Route;

import akka.util.Timeout;

import spray.json.DefaultJsonProtocol._;

import scala.concurrent.duration._;
import scala.concurrent.{ExecutionContext, Future};
import scala.io.StdIn;
import scala.language.postfixOps;
import scala.util.{Failure, Success};

import com.rtbkg.BiddingActor;
import com.rtbkg.{Campaign, Banner, Impression, BidRequest, BidResponse};
import com.rtbkg.jsonFormats._;

object BiddingAgent {
  implicit val bidReqFormat = jsonFormat5(BidRequest);
  implicit val bidRespFormat = jsonFormat5(BidResponse);

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[BiddingActor.AttemptBid] = ActorSystem(BiddingActor.apply, "rtbkgsys");
    implicit val executionContext: ExecutionContext = system.executionContext;
    val rtbActor: ActorRef[BiddingActor.AttemptBid] = system;

    val route: Route =
      path("bid") {
        post {
          implicit val timeout: Timeout = 5 seconds;

          entity(as[BidRequest]) { req => 
            onComplete(
              rtbActor.ask(replyTo => BiddingActor.AttemptBid(req, replyTo))
            ){
              case Failure(exception) => 
                complete(StatusCodes.InternalServerError);
              case Success(BiddingActor.BidComplete(matchingCampaign)) =>
                matchingCampaign match {
                  case None =>
                    complete(StatusCodes.NoContent);
                  case Some(mtch @ BidResponse(_, _, _, _, _)) =>
                    complete(mtch);
                }
            }
            
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