//package com.saranaresturantsystem.specification.catalog.productVariant;
//
//import com.saranaresturantsystem.entities.catalog.ProductVariant;
//import org.springframework.data.jpa.domain.Specification;
//
//public class ProductVariantSpec {
//    public  static Specification<ProductVariant> filterBy(ProductVariantFilter filter){
//        return  (root, query, cb) -> {
//            if (filter==null){
//                return  cb.conjunction();
//            }
//            var predicates  = cb.conjunction();
//            if (filter.code() != null && !filter.code().isEmpty()) {
//                predicates = cb.and(
//                        predicates,
//                        cb.like(cb.upper(root.get("code")), "%" + filter.code().toUpperCase() + "%")
//                );
//            }
//            if (filter.sellingPrice() != null && !filter.sellingPrice().isEmpty()) {
//                predicates = cb.and(
//                        predicates,
//                        cb.like(cb.upper(root.get("sellingPrice")), "%" + filter.sellingPrice().toUpperCase() + "%")
//                );
//            }
//            if (filter.status() != null && !filter.status().isEmpty()) {
//                predicates = cb.and(
//                        predicates,
//                        cb.like(cb.upper(root.get("status")), "%" + filter.status().toUpperCase() + "%")
//                );
//            }
//
//            return  predicates;
//        };
//    }
//}
