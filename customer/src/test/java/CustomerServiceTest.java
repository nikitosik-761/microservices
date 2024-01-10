import org.amqp.RabbitMQMessageProducer;
import org.clients.fraud.FraudCheckResponse;
import org.clients.fraud.FraudClient;
import org.clients.notification.NotificationRequest;
import org.customer.models.Customer;
import org.customer.models.CustomerRegistrationRequest;
import org.customer.repositories.CustomerRepository;
import org.customer.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;


    @Mock
    private FraudClient fraudClient;

    @Mock
    private RabbitMQMessageProducer producer;


    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerRepository, fraudClient, producer);
    }

    @Test
    void saveCustomer(){
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                "Name",
                "LastName",
                "Email"
        );


        Customer customer = Customer.builder()
                .firstname(registrationRequest.firstname())
                .lastname(registrationRequest.lastname())
                .email(registrationRequest.email())
                .build();

        when(fraudClient.isFraudster(customer.getId())).thenReturn(new FraudCheckResponse(false));

        customerService.registerCustomer(registrationRequest);


        verify(customerRepository, times(1)).saveAndFlush(customer);


        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi, %s, welcome to Helll!!!",
                        customer.getFirstname()

                )
        );

        assertThat(notificationRequest.message()).isEqualTo(
                String.format("Hi, %s, welcome to Helll!!!",
                customer.getFirstname()

        ));

        verify(producer, times(1)).publish(
                ArgumentMatchers.eq(notificationRequest),
                ArgumentMatchers.eq("internal.exchange"),
                ArgumentMatchers.eq("internal.notification.routing-key")
        );
    }

    @Test
    void throwExceptionWhenCustomerIsFraudster(){

        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                "Name",
                "LastName",
                "Email"
        );

        Customer customer = Customer.builder()
                .firstname(registrationRequest.firstname())
                .lastname(registrationRequest.lastname())
                .email(registrationRequest.email())
                .build();

        when(fraudClient.isFraudster(customer.getId())).thenReturn(new FraudCheckResponse(true));


        assertThatThrownBy(() -> {
            customerService.registerCustomer(registrationRequest);
        }).hasMessageContaining("fraudster").isInstanceOf(IllegalStateException.class);




    }


}
