package school.faang.user_service.service.country;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public Country findByTitle(String titleName) {
        return countryRepository.findByTitle(titleName)
                .orElseGet(() -> createCountry(titleName));
    }

    private Country createCountry(String titleName) {
        log.info("Country with title '{}' not found, creating new entry", titleName);
        Country newCountry = new Country();
        newCountry.setTitle(titleName);
        return countryRepository.save(newCountry);
    }
}
