package org.minnal.jaxrs.test;

import org.activejpa.enhancer.ActiveJpaAgentLoader;
import org.activejpa.jpa.JPA;
import org.activejpa.jpa.JPAContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.minnal.jaxrs.test.provider.JacksonProvider;
import org.testng.IObjectFactory;
import org.testng.ITestContext;
import org.testng.annotations.ObjectFactory;
import org.testng.internal.ObjectFactoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public abstract class BaseJPAResourceTest extends BaseResourceTest {

    /**
     * Note: Kind of hack to ensure that ActiveJPAAgent instruments all the models before they are loaded.
     *
     * @param context
     * @return
     * @throws Exception
     */
    @ObjectFactory
    public IObjectFactory getObjectFactory(ITestContext context) throws Exception {
        ActiveJpaAgentLoader.instance().loadAgent();
        return new ObjectFactoryImpl();
    }

    public void init(ResourceConfig resourceConfig, JacksonProvider provider) {
        super.init(resourceConfig, provider);
    }

    public void setup() {
        super.setup();
        if (!disableForeignKeyChecks()) {
            return;
        }

        JPAContext context = JPA.instance.getDefaultConfig().getContext();
        context.beginTxn();
        try {
            EntityManager manager = context.getEntityManager();
            Query query = manager.createNativeQuery("SET DATABASE REFERENTIAL INTEGRITY FALSE");
            query.executeUpdate();
        } finally {
            context.closeTxn(false);
        }
    }

    /**
     * Override this method if you don't want to disable foreign key checks
     *
     * @return
     */
    protected boolean disableForeignKeyChecks() {
        return true;
    }

}