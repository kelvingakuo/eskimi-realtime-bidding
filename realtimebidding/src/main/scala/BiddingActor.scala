package com.rtbkg;

import akka.actor.typed.{ActorRef, Behavior};
import akka.actor.typed.scaladsl.Behaviors

import com.rtbkg.{Campaign, Banner, Impression, BidRequest, BidResponse};
import com.rtbkg.campaigns;

import java.util.UUID;
import scala.util.Random;
// Compare bid request with available campaigns

object BiddingActor {
  sealed trait Message;
  case class AttemptBid(bid: BidRequest, replyTo: ActorRef[BidComplete]) extends Message;
  case class BidComplete(theMatch: Option[BidResponse]) extends Message;

  def apply(): Behavior[AttemptBid] = 
    Behaviors.receiveMessage {
      case AttemptBid(bid, replyTo) => 
        val campaignsThatMatch = filterCampaigns(bid);
        replyTo ! BidComplete(campaignsThatMatch);
        Behaviors.same;
    }

    def checkBidPrice(campaign: Campaign, impressions: Option[List[Impression]]): List[Impression] = {
      impressions.getOrElse(List.empty[Impression]).filter(impression =>
        impression.bidFloor.get <= campaign.bid
      );
    }

    case class RightBanner(banner: Banner, id: Int, price: Option[Double]);
    def getRightSizeBanners(campaign: Campaign, impressions: Seq[Impression]) = {
      for{
        impression <- impressions
        banner <- campaign.banners
        if (banner.width == impression.w.get && banner.height == impression.h.get) || ((impression.wmin.get <= banner.width && impression.wmax.get >= banner.width) && (impression.hmin.get <= banner.height && impression.hmax.get >= banner.height)) // Returns the first matching banner
      } yield RightBanner(banner, campaign.id, impression.bidFloor); //No other data type was working
    }
  
    def filterCampaigns(req: BidRequest): Option[BidResponse] = {
      // Filter by site ID and country
      val campsBySiteAndCountry = campaigns.activeCampaigns.filter(campaign => 
        campaign.country == req.country  &&
        campaign.targeting.targetedSiteIds.contains(req.site.id)
      );
      
      // Filter by affordability i.e. campaign bid >= bid request floor
      val affordableImpressions = campsBySiteAndCountry.flatMap(campaign =>
        checkBidPrice(campaign, req.imp)
      );

      // Filter by size of banner
      val allMatchingCampaigns = campsBySiteAndCountry.flatMap(campaign => 
        getRightSizeBanners(campaign, affordableImpressions)
      );
      
      if(allMatchingCampaigns.isEmpty){
        // Return None if no matching campaigns
        None
      }else{
        // Pick one random campaign
        val random = new Random;
        val oneCampaign = allMatchingCampaigns(
            random.nextInt(allMatchingCampaigns.length)
        );

        // Construct Option[BidResponse]
        Some(
          BidResponse(
            id = UUID.randomUUID().toString,
            bidRequestId = Some(req.id),
            price = oneCampaign.price,
            adId = Some(oneCampaign.id.toString),
            banner = Some(
              oneCampaign.banner
            )
          )
        );
      }
    }
}