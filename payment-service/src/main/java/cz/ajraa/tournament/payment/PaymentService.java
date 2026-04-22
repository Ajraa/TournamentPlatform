package cz.ajraa.tournament.payment;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
}
