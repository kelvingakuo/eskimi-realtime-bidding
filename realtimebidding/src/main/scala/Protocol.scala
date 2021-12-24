package com.rtbkg;
// Define all the relevant elements i.e. bid, campaign, bid response etc.

// Campaign protocol
case class Targeting (
    targetSiteIds: Seq[String]
    // TODO: Add other matching criteria
);

case class Banner (
    id: Int,
    src: String,
    width: Int,
    height: Int
);

case class Campaign (
    id: Int,
    country: String,
    targeting: Targeting,
    banners: List[Banner],
    bid: Double
);

// Bid request protocol
case class Impression (
    id: String,
    wmin: Option[Int],
    wmax: Option[Int],
    w: Option[Int],
    hmin: Option[Int],
    hmax: Option[Int],
    h: Option[Int],
    bidFloor: Option[Double]
);

case class Site (
    id: String,
    domain: String,
);

case class Geo (
    country: Option[String]
);

case class User (
    id: String,
    geo: Option[Geo]
);

case class Device (
    id: String,
    geo: Option[Geo]
);

case class BidRequest (
    id: String,
    imp: Option[List[Impression]],
    site: Site,
    user: Option[User],
    device: Option[Device]
);

case class MatchingCampaign (
    id: String,
    bidRequestId: Option[String],
    price: Option[Double],
    adId: Option[String],
    banner: Option[Banner]


)