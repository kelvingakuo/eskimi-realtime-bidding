package com.rtbkg;

import com.rtbkg.{Campaign, Targeting, Banner};

// TODO: Create a list of reusable banners

// A list of active campaigns
object campaigns {
    val activeCampaigns = Seq(
        Campaign (
            id = 1,
            country = "LT",
            targeting = Targeting(
                targetSiteIds = Seq("aa", "bb", "cc")
            ),
            banners = List(
                Banner (
                    id = 1,
                    src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg",
                    width = 300,
                    height = 250
                
                )
            ),
            bid = 5
        ),
        Campaign (
            id = 2,
            country = "LT",
            targeting = Targeting(
                targetSiteIds = Seq("0006a522ce0f4bbbbaa6b3c38cafaa0f", "dd", "ee")
            ),
            banners = List(
                Banner (
                    id = 1,
                    src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg",
                    width = 300,
                    height = 250
                
                )
            ),
            bid = 5
        )
    );
}