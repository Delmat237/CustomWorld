package com.customworld.service;

import com.customworld.dto.request.ReviewRequest;
import com.customworld.dto.response.ReviewResponse;

import java.util.List;
import java.util.Map;

public interface ProductInteractionService {

    Map<String, Object> toggleLike(Long productId);

    long getLikeCount(Long productId);

    boolean hasCurrentUserLiked(Long productId);

    ReviewResponse addReview(Long productId, ReviewRequest request);

    List<ReviewResponse> getProductReviews(Long productId);

    void deleteReview(Long reviewId);

    static int calculateRating(long likeCount) {
        if (likeCount == 0) return 0;
        if (likeCount < 5) return 1;
        if (likeCount < 15) return 2;
        if (likeCount < 30) return 3;
        if (likeCount < 50) return 4;
        return 5;
    }
}
