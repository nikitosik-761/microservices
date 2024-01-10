import fraud.models.FraudCheckHistory;
import fraud.repositories.FraudCheckHistoryRepository;
import fraud.services.FraudCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class FraudCheckServiceTest {
    @Mock
    private FraudCheckHistoryRepository repository;

    private FraudCheckService service;

    @BeforeEach
    void setUp() {
        service = new FraudCheckService(repository);
    }

    @Test
    void saveCheckHistory(){

       FraudCheckHistory fraudCheckHistory = FraudCheckHistory.builder()
                .customerId(1)
                .isFraudster(false)
                .createdAt(LocalDateTime.now())
                .build();

        boolean isFraudster = service.isFraudulent(1);

        verify(repository, times(1)).save(fraudCheckHistory);

        assertFalse(isFraudster);

    }
}
