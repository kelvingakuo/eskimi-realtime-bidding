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
                val matches = filterCampaigns(bid)
                replyTo ! BidComplete(matches)
                Behaviors.same
        }

        def checkBidPrice(campaign: Campaign, impressions: Option[List[Impression]]): List[Impression] = {
            impressions.getOrElse(List.empty[Impression]).filter(impression =>
                impression.bidFloor.get <= campaign.bid
            );
        }

        def getRightSizeBanners(campaign: Campaign, impressions: Seq[Impression]) = {
            class FinalCamp(banner: Banner, adId: String, price: Double);
            for{
                impression <- impressions
                banner <- campaign.banners
                if (banner.width == impression.w.get && banner.height == impression.h.get) || ((impression.wmin.get <= banner.width && impression.wmax.get >= banner.width) && (impression.hmin.get <= banner.height && impression.hmax.get >= banner.height))
             } yield FinalCamp(banner, campaign.id, impression.bidFloor)
        }
    
        def filterCampaigns(req: BidRequest): Option[BidResponse] = {
            // Filter by site ID and country
            val campsBySiteAndCountry = campaigns.activeCampaigns.filter(campaign => 
                campaign.country == req.country  &&
                campaign.targeting.targetedSiteIds.contains(req.site.id)
            );
            
            val affordableImpressions = campsBySiteAndCountry.flatMap(campaign =>
                checkBidPrice(campaign, req.imp)
            );

            val allMatchingCampaigns = campsBySiteAndCountry.flatMap(campaign => 
                getRightSizeBanners(campaign, affordableImpressions)
            );
            
            if(allMatchingCampaigns.isEmpty){
                None
            }else{
                val random = new Random;
                val oneCampaign = allMatchingCampaigns(
                    random.nextInt(allMatchingCampaigns.length)
                );
                print(oneCampaign);
                Some(
                    BidResponse(
                        id = UUID.randomUUID().toString,
                        bidRequestId = Some(req.id),
                        price = Some(oneCampaign.price),
                        adId = Some(oneCampaign.adId),
                        banner = Some(
                            oneCampaign.banner
                        )
                    )
                );
            }
        }
}