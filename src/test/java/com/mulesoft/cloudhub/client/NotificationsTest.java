package com.mulesoft.cloudhub.client;

import static junit.framework.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class NotificationsTest
{

    public static final String APP_NAME = "cloudhub-notifications-connection-api-test-app";
    private static Connection cloudhubConnection = new Connection("https://cloudhub.io/", "cloudhub-prodev", "Cl0udhub", true);

    @BeforeClass
    public static void createApplicationForTest()
    {
        Application testApplication = new Application();
        testApplication.setDomain(APP_NAME);
        testApplication.setHasFile(false);
        testApplication.setWorkers(1);

        try
        {
            cloudhubConnection.createApplication(testApplication);
        }
        catch (Exception e){}
    }

    @Test
    public void testCreateTenantSuccessfully()
    {
        Notification notification = new Notification();
        notification.setMessage("Hello World");
        notification.setId("HellId");
        notification.setDomain(APP_NAME);

        Notification notificationResponse = cloudhubConnection.on(APP_NAME).create(notification);

        assertEquals("Hello World", notificationResponse.getMessage());

    }

    @AfterClass
    public static void undeployTestApp()
    {
        cloudhubConnection.on(APP_NAME).delete();

    }
}
