package edu.pk.qurduplex.identityService.repositories;

import edu.pk.qurduplex.identityService.models.VerificationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VerificationCodeRepository extends CrudRepository<VerificationCode, UUID> {
}
