package com.AIve.consumer.dto.Marketaux;

import java.util.ArrayList;

public record News(String uuid,
                   String title,
                   String description,
                   String keywords,
                   String snippet,
                   String url,
                   String image_url,
                   String language,
                   String published_at,
                   String source,
                   String relevance_score,
                   ArrayList<Object> entities,
                   ArrayList<Object> similar
) {
}
