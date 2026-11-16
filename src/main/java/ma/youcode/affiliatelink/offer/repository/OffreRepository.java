package ma.youcode.affiliatelink.offer.repository;

import ma.youcode.affiliatelink.common.OffreSummaryDto;
import ma.youcode.affiliatelink.offer.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;



public interface OffreRepository extends JpaRepository<Offre, Long>, JpaSpecificationExecutor<Offre> {

    @Query("SELECT new ma.youcode.affiliatelink.common.OffreSummaryDto(o.id ,o.title ,o.description) FROM Offre o WHERE o.id = :id")
    List<OffreSummaryDto> getOfferDtoById(@Param("id") Long id);
}

