import io.dropwizard.Configuration;
import javax.validation.constraints.NotEmpty;

public class DiretorioConfiguration extends Configuration {
    @NotEmpty
    public String template;

    @NotEmpty
    public String defaultName = "Stranger";
}
