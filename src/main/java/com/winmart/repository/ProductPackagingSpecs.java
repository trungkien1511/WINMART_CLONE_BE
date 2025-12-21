package com.winmart.repository;

import com.winmart.entity.ProductPackaging;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductPackagingSpecs {

    public static Specification<ProductPackaging> distinct() {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.conjunction();
        };
    }

    public static Specification<ProductPackaging> productActive() {
        return (root, query, cb) ->
                cb.isTrue(root.join("product").get("isActive")); // pp.product.isActive
    }

    public static Specification<ProductPackaging> categoryOrParentSlug(String slug) {
        return (root, query, cb) -> {
            var p = root.join("product");                       // pp.product
            var pc = p.join("productCategories");               // product.productCategories
            var c = pc.join("category");                        // productCategory.category
            var parent = c.join("parent", JoinType.LEFT);       // category.parent

            return cb.and(
                    cb.isTrue(c.get("isActive")),               // category.isActive
                    cb.or(
                            cb.equal(c.get("slug"), slug),
                            cb.equal(parent.get("slug"), slug)
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
