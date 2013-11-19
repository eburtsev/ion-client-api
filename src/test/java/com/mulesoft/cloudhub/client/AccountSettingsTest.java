/**
 *
 */
package com.mulesoft.cloudhub.client;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class AccountSettingsTest
{

    private Connection connection = new Connection(System.getProperty("ch.test.url"), System.getProperty("ch.user"),
                                                           System.getProperty("ch.password"), true);

    @Test
    public void testAccountSettings()
    {


        Account account = connection.getAccount();
        System.out.println("account = " + account);
    }

    @Test
    public void testDomainAvailable()
    {
        boolean account = connection.on("lalalalala").available();
        System.out.println("account = " + account);
    }


    @Test
    public void testCreateDomain()
    {
        Application application = new Application();
        String domain = "lalalalala5";
        application.setDomain(domain);
        application.setDescription("test");
        application.setWorkers(1);
        boolean available = connection.on(domain).available();
        System.out.println("available = " + available);
        Application application1 = connection.createApplication(application);
        System.out.println("application1 = " + application1);

        available = connection.on(domain).available();
        System.out.println("available = " + available);
    }

    @Test
    @Ignore
    public void testStatus()
    {
        Application application = new Application();
        String domain = "lalalalala2";
        application.setDomain(domain);
        application.setDescription("test");
        application.setWorkers(1);
        Application.Status available = connection.on(domain).status();
        System.out.println("status = " + available);

    }

    @Test
    @Ignore
    public void testGetAction(){
        Application cloudHubApplication = connection.on("test").get();
        System.out.println("cloudHubApplication = " + cloudHubApplication);
    }

    @Test
    public void testSupportedMuleVersions(){
        List<String> supportedApplications = connection.getSupportedMuleVersions();
        System.out.println("supportedApplications = " + supportedApplications);

    }
}
