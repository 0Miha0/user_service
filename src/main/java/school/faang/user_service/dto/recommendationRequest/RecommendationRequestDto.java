package school.faang.user_service.dto.recommendationRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestDto {

    private Long id;

    @NotBlank
    private String message;

    @NotBlank
    @NotNull
    private RequestStatus status;

    private List<Long> skills;

    @NotNull
    private Long requester;

    @NotNull
    private Long receiver;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss"  )
    private LocalDateTime createdAt;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss"  )
    private LocalDateTime updateAt;
}
