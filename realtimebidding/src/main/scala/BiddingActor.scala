package com.rtbkg;

import akka.actor.typed.{ActorRef, Behavior};
import akka.actor.typed.scaladsl.Behaviors

import com.rtbkg.{Campaign, Banner, Impression, BidRequest};
import com.rtbkg.campaigns;
// Compare bid request with available campaigns

object BiddingActor {

    sealed trait Request;
    case class AttemptBid(bid: BidRequest, replyTo: ActorRef[BidComplete]) extends Request;

    sealed trait Response;
    case class BidComplete(theMatch: MatchingCampaign) extends Response;

    def apply(): Behavior[AttemptBid] = 
        Behaviors.receiveMessage {
            case AttemptBid(bid, replyTo) => 
                val matches = filterCampaigns(bid)
                replyTo ! BidComplete(matches)
                Behaviors.same
        }
    
        def filterCampaigns(req: BidRequest): MatchingCampaign = {
            // Filter by site ID and country
            val BySiteCountry = campaigns.filter(campaign => 
                campaign.country == req.country &&
                campaign.targeting.targetedSiteIds.contains(req.site.id)
            );
            println(BySiteCountry);
        }
}