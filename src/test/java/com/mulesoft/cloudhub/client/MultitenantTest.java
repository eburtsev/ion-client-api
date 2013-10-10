package com.mulesoft.cloudhub.client;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MultitenantTest
{

    public static final String APP_NAME = "cloudhub-multitenant-connection-api-test-app";
    public static final String TENANT_ID = "tenantTestId";
    public static final String TENANT_EMAIL = "test@organization.com";
    public static final String COMPANY_NAME = "mulesoft";
    private static Connection cloudhubConnection = new Connection("https://dev.cloudhub.io/", "cloudhub-rest", "Cl0udhub", true);

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
        Tenant createdTenant = cloudhubConnection.on(APP_NAME).create(tenant(), APP_NAME);

        assertEquals(TENANT_EMAIL, createdTenant.getEmail());
        assertEquals(TENANT_ID, createdTenant.getId());
        assertEquals(COMPANY_NAME, createdTenant.getName());
    }

    @Test
    public void testCreateTenantSuccessfullyWithOldConfiguration()
    {
        Tenant tenant = new Tenant();
        tenant.setContactEmail(TENANT_EMAIL);
        tenant.setId(TENANT_ID+"2");
        tenant.setCompanyName(COMPANY_NAME);

        Tenant createdTenant = cloudhubConnection.on(APP_NAME).create(tenant, APP_NAME);

        assertEquals(TENANT_EMAIL, createdTenant.getEmail());
        assertEquals(TENANT_ID+"2", createdTenant.getId());
        assertEquals(COMPANY_NAME, createdTenant.getName());
    }


    @Test
    public void testCreateTenantForInvalidApp()
    {
        try
        {
            cloudhubConnection.on("invalid-test-tenant-app").create(tenant(), "invalid-test-tenant-app");
            fail("Exception must be thrown when tenant cannot be created");
        }
        catch (CloudHubException e)
        {
            assertEquals("That resource was not found.", e.getMessage());
        }
    }

    @Test
    public void testCreateInvalidTenant()
    {
        try
        {
            cloudhubConnection.on(APP_NAME).create(new Tenant(), APP_NAME);
            fail("Exception must be thrown when tenant cannot be created");
        }
        catch (CloudHubException e)
        {
            assertEquals("id is empty or incorrect.", e.getMessage());
        }
    }


    @Test
    public void testGetTenantList()
    {
        insertTenantIgnoringFailure();

        TenantResults tenantResults = cloudhubConnection.on(APP_NAME).listTenants(APP_NAME, 10, 0, null);

        assertEquals(new Long(2), tenantResults.getTotal());
        assertEquals(TENANT_ID.toLowerCase(), tenantResults.getData().get(0).getId());
    }

    @Test
    public void testGetTenant()
    {
        insertTenantIgnoringFailure();

        Tenant tenant = cloudhubConnection.on(APP_NAME).getTenant(APP_NAME, TENANT_ID.toLowerCase());

        assertEquals(TENANT_ID.toLowerCase(), tenant.getId());
    }


    @Test
    public void testUpdateTenant()
    {
        insertTenantIgnoringFailure();

        Tenant requestTenant = tenant();
        requestTenant.setEmail("another@company.com");
        Tenant tenant = cloudhubConnection.on(APP_NAME).update(requestTenant, APP_NAME);

        assertEquals("another@company.com", tenant.getEmail());
    }

    private void insertTenantIgnoringFailure()
    {
        try {
            cloudhubConnection.on(APP_NAME).create(tenant(), APP_NAME);
        }
        catch (CloudHubException e)
        {
            // Ignore in case tenant already exists
        }
    }

    private Tenant tenant()
    {
        Tenant tenant = new Tenant();
        tenant.setEmail(TENANT_EMAIL);
        tenant.setId(TENANT_ID);
        tenant.setName(COMPANY_NAME);
        return tenant;
    }


    @AfterClass
    public static void undeployTestApp()
    {
        cloudhubConnection.on(APP_NAME).delete();

    }
}
