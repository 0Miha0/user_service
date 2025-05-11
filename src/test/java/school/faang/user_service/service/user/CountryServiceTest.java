package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    @Test
    void getOrCreateCountryWhenExist() {
        String title = "USA";
        Country existingCountry = new Country();
        existingCountry.setTitle(title);

        when(countryRepository.findByTitle(title)).thenReturn(Optional.of(existingCountry));
        Country result = countryService.getOrCreateCountry(title);

        assertEquals(title, result.getTitle());

        verify(countryRepository, times(1)).findByTitle(title);
        verify(countryRepository, times(0)).save(any(Country.class));
    }

    @Test
    void getOrCreateCountryNewCountry() {
        String title = "Canada";
        when(countryRepository.findByTitle(title)).thenReturn(Optional.empty());

        Country country = new Country();
        country.setTitle(title);
        when(countryRepository.save(any(Country.class))).thenReturn(country);
        Country result = countryService.getOrCreateCountry(title);

        assertEquals(title, result.getTitle());

        verify(countryRepository, times(1)).findByTitle(title);
        verify(countryRepository, times(1)).save(any(Country.class));
    }

}
