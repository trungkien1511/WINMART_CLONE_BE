package com.winmart.product.query.spec;

import com.winmart.product.domain.ProductPackaging;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductPackagingSpecs {

    public static Specification<ProductPackaging> distinct() {
        return (root, query, criteriaBuilder) -> {
            if (query != null) query.distinct(true);
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<ProductPackaging> productActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.join("product").get("isActive"));
    }

    public static Specification<ProductPackaging> categoryOrParentSlug(String slug) {
        return (root, query, criteriaBuilder) -> {
            var p = root.join("product");
            var pc = p.join("productCategories");
            var c = pc.join("category");
            var parent = c.join("parent", JoinType.LEFT);

            return criteriaBuilder.and(
                    criteriaBuilder.isTrue(c.get("isActive")),
                    criteriaBuilder.or(
                            criteriaBuilder.equal(c.get("slug"), slug),
                            criteriaBuilder.equal(parent.get("slug"), slug)
                    )
            );

        };
    }

    public static Specification<ProductPackaging> brandSlugIn(List<String> brandSlugs) {
        return (root, query, cb) -> {
            if (brandSlugs == null || brandSlugs.isEmpty()) {
                return cb.conjunction();
            }
            var p = root.join("product");                       // pp.product
            var b = p.join("brand");                            // product.brand
            return b.get("slug").in(brandSlugs);                // brand.slug
        };
    }
}
