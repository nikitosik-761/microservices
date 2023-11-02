package org.customer.services;

import org.amqp.RabbitMQMessageProducer;
import org.customer.models.Customer;
import org.customer.models.CustomerRegistrationRequest;
import org.customer.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.clients.fraud.FraudCheckResponse;
import org.clients.fraud.FraudClient;
import org.clients.notification.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomerService{

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer mqMessageProducer;
    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    public void registerCustomer(CustomerRegistrationRequest registrationRequest) {
        Customer customer = Customer.builder()
                .firstname(registrationRequest.firstname())
                .lastname(registrationRequest.lastname())
                .email(registrationRequest.email())
                .build();

        customerRepository.saveAndFlush(customer); //чтобы находилось в сессии

        FraudCheckResponse fraudCheckResponse =
                fraudClient.isFraudster(customer.getId()); //с помощью flueth

        if (fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster");
        }

        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi, %s, welcome to Helll!!!",
                        customer.getFirstname()

                )
        );


        kafkaTemplate.send("notific", notificationRequest);


        mqMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
                );




    }
}
