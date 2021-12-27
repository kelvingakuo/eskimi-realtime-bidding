package com.rtbkg;

import akka.actor.typed.{ActorRef, Behavior};
import akka.actor.typed.scaladsl.Behaviors

import com.rtbkg.{Campaign, Banner, Impression, BidRequest, BidResponse};
import com.rtbkg.campaigns;

import java.util.UUID;
// Compare bid request with available campaigns

object BiddingActor {

    sealed trait Message;
    case class AttemptBid(bid: BidRequest, replyTo: ActorRef[BidComplete]) extends Message;
    case class BidComplete(theMatch: Option[BidResponse]) extends Message;

    def apply(): Behavior[AttemptBid] = 
        Behaviors.receiveMessage {
            case AttemptBid(bid, replyTo) => 
                val matches = filterCampaigns(bid)
                replyTo ! BidComplete(matches)
                Behaviors.same
        }
    
        def filterCampaigns(req: BidRequest): Option[BidResponse] = {
            // Filter by site ID and country
            val BySiteAndCountry = campaigns.activeCampaigns.filter(campaign => 
                campaign.country == req.country  &&
                campaign.targeting.targetSiteIds.contains(req.site.id)
            );
                     // Filter banner and bid by impression size and bid floor, respectively

            // For impressions in bid request, and banners in campaign, find the impression whose floor is <= campaign bid AND impression whose dimensions match a banner size

            // Pick one campaign
            // Dummy return
            Some(
                BidResponse(
                    id = UUID.randomUUID().toString,
                    bidRequestId = Some(req.id),
                    price = Some(6.0),
                    adId = Some("bleh"),
                    banner = Some(
                        Banner (
                            id = 1,
                            src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg",
                            width = 300,
                            height = 250
                        
                        )
                    )
                )
            )
        }
}