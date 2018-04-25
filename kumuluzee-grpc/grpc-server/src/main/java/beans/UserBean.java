package beans;

import entity.User;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

@ApplicationScoped
public class UserBean {

    private static final Logger logger = Logger.getLogger(UserBean.class.getName());

    @PersistenceContext(name = "sample-user-jpa")
    private EntityManager em;

    @PostConstruct
    private void init() {
        logger.info("UserBean initialized");
    }

    public User getUser(Integer id) {
        return em.find(User.class, id);
    }

}
