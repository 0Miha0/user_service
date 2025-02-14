package school.faang.user_service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "dicebear")
public class DiceBearProperties {
    private String baseUrl;
    private String version;
    private String style;
    private String format;
}
