package cz.ajraa.tournament.payment;

import io.micronaut.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentTransaction, Long> {
}
