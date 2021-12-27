package com.rtbkg;

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._;
import spray.json.DefaultJsonProtocol._;
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
    device: Option[Device],
) {
    def country: String = {
        val userC = user.get.geo.get.country.get;
        val devC = device.get.geo.get.country.get;
        
        if(devC.isEmpty) userC else devC;
    }
}

// JSON marshalling
object jsonFormats {
    implicit val geoFormat = jsonFormat1(Geo);
    implicit val deviceFormat = jsonFormat2(Device);
    implicit val userFormat = jsonFormat2(User);
    implicit val siteFormat = jsonFormat2(Site);
    implicit val impressionFormat = jsonFormat8(Impression);

    implicit val bannerFormat = jsonFormat4(Banner);
}

// The matching campaign returned by actor
case class BidResponse (
    id: String,
    bidRequestId: Option[String],
    price: Option[Double],
    adId: Option[String],
    banner: Option[Banner]
)