package com.AIve.consumer.dto.Marketaux;

import java.util.ArrayList;

public record BatchOfNews (
        MarketauxMetaInfo info,
        ArrayList<News> data
){
}
