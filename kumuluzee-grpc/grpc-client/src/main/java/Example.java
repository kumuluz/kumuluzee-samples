import client.UserServiceClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.logging.Logger;

@ApplicationScoped
public class Example {

    private final static Logger logger = Logger.getLogger(Example.class.getName());

    @Inject
    UserServiceClient userClient;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object o){
        logger.info("Example initialized");
        userClient.getUser(1);
    }
}
