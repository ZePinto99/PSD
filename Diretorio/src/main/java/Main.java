import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import resources.DiretorioResource;
import Testes.TemplateHealthCheck;

public class Main extends Application<DiretorioConfiguration> {

    public static void main(final String[] args) throws Exception {
        new Main().run(args);
    }

    @Override
    public String getName() {
        return "Diretorio";
    }

    @Override
    public void initialize(final Bootstrap<DiretorioConfiguration> bootstrap) {
    }

    @Override
    public void run(final DiretorioConfiguration configuration, final Environment environment) {

        environment.jersey().register(new DiretorioResource());
        environment.healthChecks().register("template", new TemplateHealthCheck(configuration.template));
    }

}
