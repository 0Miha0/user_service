package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RejectionDto {
    @NotBlank
    private String reason;
}
