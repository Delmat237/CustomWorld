package com.customworld.service.impl;

import com.customworld.dto.request.ReviewRequest;
import com.customworld.dto.response.ReviewResponse;
import com.customworld.entity.Product;
import com.customworld.entity.ProductLike;
import com.customworld.entity.ProductReview;
import com.customworld.entity.User;
import com.customworld.exception.ResourceNotFoundException;
import com.customworld.repository.ProductLikeRepository;
import com.customworld.repository.ProductRepository;
import com.customworld.repository.ProductReviewRepository;
import com.customworld.repository.UserRepository;
import com.customworld.service.ProductInteractionService;
import com.utils.UserInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductInteractionServiceImpl implements ProductInteractionService {

    private static final Logger log = LoggerFactory.getLogger(ProductInteractionServiceImpl.class);

    private final ProductLikeRepository likeRepository;
    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductInteractionServiceImpl(ProductLikeRepository likeRepository,
                                         ProductReviewRepository reviewRepository,
                                         ProductRepository productRepository,
                                         UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Object> toggleLike(Long productId) {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        boolean liked;
        if (likeRepository.existsByProductIdAndUserId(productId, user.getId())) {
            likeRepository.deleteByProductIdAndUserId(productId, user.getId());
            liked = false;
            log.info("User {} unliked product {}", user.getId(), productId);
        } else {
            likeRepository.save(ProductLike.builder()
                    .product(product)
                    .user(user)
                    .build());
            liked = true;
            log.info("User {} liked product {}", user.getId(), productId);
        }

        long likeCount = likeRepository.countByProductId(productId);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", likeCount);
        result.put("rating", ProductInteractionService.calculateRating(likeCount));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCount(Long productId) {
        return likeRepository.countByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCurrentUserLiked(Long productId) {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        return likeRepository.existsByProductIdAndUserId(productId, user.getId());
    }

    @Override
    public ReviewResponse addReview(Long productId, ReviewRequest request) {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        ProductReview review = ProductReview.builder()
                .product(product)
                .user(user)
                .content(request.getContent())
                .build();

        review = reviewRepository.save(review);
        log.info("User {} added review on product {}", user.getId(), productId);
        return convertToReviewResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::convertToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReview(Long reviewId) {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis non trouvé"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Vous n'êtes pas autorisé à supprimer cet avis");
        }

        reviewRepository.deleteById(reviewId);
        log.info("Review {} deleted by user {}", reviewId, user.getId());
    }

    private ReviewResponse convertToReviewResponse(ProductReview review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
