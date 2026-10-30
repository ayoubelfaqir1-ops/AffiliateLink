package ma.youcode.affiliatelink.offer.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import ma.youcode.affiliatelink.common.enums.Niche;
import ma.youcode.affiliatelink.offer.entity.Offre;
import ma.youcode.affiliatelink.offer.entity.Tag;
import org.springframework.data.jpa.domain.Specification;


public class OffreSpecification {

    public static Specification<Offre> hasNiche(Niche niche) {
        return (root, query, cb) -> 
            niche == null ? null : cb.equal(root.get("niche"), niche);
    }

    public static Specification<Offre> hasAdvertiserId(Long advertiserId) {
        return (root, query, cb) -> 
            advertiserId == null ? null : cb.equal(root.get("advertiserId"), advertiserId);
    }

    public static Specification<Offre> hasTag(String tag) {
        return (root, query, cb) -> {
            if (tag == null || tag.isBlank()) return null;
            query.distinct(true);
            Join<Offre, Tag> tagJoin = root.join("tags");
            return cb.equal(cb.lower(tagJoin.get("name")), tag.trim().toLowerCase());
        };
    }

    public static Specification<Offre> containsKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
            Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
            return cb.or(titleMatch, descMatch);
        };
    }
}