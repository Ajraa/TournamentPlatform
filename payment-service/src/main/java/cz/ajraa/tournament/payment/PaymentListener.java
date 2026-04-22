package cz.ajraa.tournament.payment;

import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RabbitListener
@RequiredArgsConstructor
public class PaymentListener {

    private final PaymentService paymentService;

    @Queue("payment.queue")
    public void receivePaymentEvent(byte[] data) {
        String message = new String(data);
        log.info("Obsah: {}", message);
    }

}
