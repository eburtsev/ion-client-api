/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.mulesoft.cloudhub.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.mulesoft.cloudhub.client.ApplicationStatusChange.ApplicationStatus;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Extends {@class Connection} by providing domain level operations.
 */
public class DomainConnection extends Connection {

    public static final String DEFAULT_MULE_VERSION = "3.2.1";
    public static final int DEFAULT_WORKERS = 1;
    public static final long DEFAULT_MAX_WAIT_TIME = 120000L;
    private final String domain;

    public DomainConnection(final Connection connection, final String domain) {
        super(connection.getUrl(), connection.getUsername(), connection.getPassword());

        if (domain == null) {
            throw new IllegalArgumentException("null domain");
        }

        this.domain = domain;
    }

    public final String getDomain() {
        return this.domain;
    }

    protected final Application getCloudHubApplication() {
        ClientResponse response = createApplicationBuilder(getDomain()).type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        handleErrors(response);
        return response.getEntity(Application.class);
    }

    public final InputStream downloadZip() {
        ensureCloudHubApplicationExists(getDomain());

        Application application = get();
        final ClientResponse response = createApplicationBuilder(getDomain() + "/download/" + application.getFilename()).get(ClientResponse.class);

        handleErrors(response);
        return response.getEntity(InputStream.class);
	}

    protected final boolean isCloudHubApplicationCreated(final String domain) {
        try {
            getCloudHubApplication();
            return true;
        } catch (CloudHubException e) {
            return false;
        }
    }

    /**
     * @param domain
     * @throws DeployableException if iON application does not exist
     */
    protected final void ensureCloudHubApplicationExists(final String domain) {
        if (!isCloudHubApplicationCreated(domain)) {
            throw new RuntimeException("CloudHub Application <"+domain+"> does not exit on <"+getUrl()+">");
        }
    }

    public final void update(final ApplicationUpdateInfo application) {
        final ClientResponse response = createApplicationBuilder(domain).type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, application);
        
        handleErrors(response);
    }

    public final void deploy(final File file) {
        deploy(file, DomainConnection.DEFAULT_MULE_VERSION, DomainConnection.DEFAULT_WORKERS, DomainConnection.DEFAULT_MAX_WAIT_TIME, Collections.EMPTY_MAP);
    }

    /**
     * Deploy provided application on specified domain.
     *
     * @param file
     * @param muleVersion
     * @param workers
     * @param maxWaitTime 
     */
    public final void deploy(final File file, final String muleVersion, final int workers, final long maxWaitTime, final Map<String, String> properties) {
        try {
            this.deployFromStream(new FileInputStream(file), file.getName(), muleVersion, workers, maxWaitTime, properties);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File does not exist",e);
        }
    }

    /**
     * Deploy provided application on specified domain based on the InputStream
     *
     * @param file
     * @param muleVersion
     * @param workers
     * @param maxWaitTime
     */
    public final void deploy(final InputStream file, final String muleVersion, final int workers, final long maxWaitTime, final Map<String, String> properties) {
        this.deployFromStream(file, getDomain() + ".zip", muleVersion, workers, maxWaitTime,properties);
    }

    private final void deployFromStream(final InputStream file, String fileName, final String muleVersion, final int workers, final long maxWaitTime, final Map<String, String> properties) {
        ensureCloudHubApplicationExists(getDomain());

        final Application application = getCloudHubApplication();
        ApplicationUpdateInfo appUdateInfo = new ApplicationUpdateInfo(application);

        if (!application.getSupportedVersions().contains(muleVersion)) {
            throw new IllegalArgumentException("Requested mule version <"+muleVersion+"> is not one of supported versions <"+application.getSupportedVersions()+">");
        }
        if (workers > application.getRemainingWorkerCount()) {
            throw new IllegalArgumentException("Requested <"+workers+"> workers but only <"+application.getRemainingWorkerCount()+"> are remaining");
        }
        switch (application.getStatus()) {
            case STARTED:
            case UNDEPLOYED:
            case DEPLOY_FAILED:
                //Update MetaData
                boolean updated = false;
                if (workers != application.getWorkers()) {
                    appUdateInfo.setWorkers(workers);
                    updated = true;
                }
                if (!muleVersion.equals(application.getMuleVersion())) {
                    appUdateInfo.setMuleVersion(muleVersion);
                    updated = true;
                }
                if (!fileName.equals(application.getFilename())) {
                    appUdateInfo.setFilename(fileName);
                    updated = true;
                }
                if (properties != null && !properties.equals(application.getProperties())) {
                    appUdateInfo.setProperties(properties);
                    updated = true;
                }

                if (updated) {
                    update(appUdateInfo);
                }

                //Push new app

                ClientResponse response = createApplicationBuilder(getDomain()+"/deploy").type(MediaType.APPLICATION_OCTET_STREAM_TYPE).post(ClientResponse.class, file);
                handleErrors(response);

                break;
            case DEPLOYING:
                throw new IllegalStateException("Another deployment is in progress");
            default:
                throw new IllegalStateException("Unhandled status <"+application.getStatus()+">");
        }

        waitForStart(maxWaitTime);
    }
    

    private void waitForStart(final long maxWaitTime) {
        if (maxWaitTime == 0) {
            return;
        }
        
        final long before = System.currentTimeMillis();
        while (System.currentTimeMillis() - before < maxWaitTime) {
            final Application latestApplication = getCloudHubApplication();
            //If application is already deployed rely on deploymentUpdateStatus
            final Application.Status status = latestApplication.getDeploymentUpdateStatus() != null ? latestApplication.getDeploymentUpdateStatus() : latestApplication.getStatus();
            switch (status) {
                case DEPLOY_FAILED:
                    //TODO extract error message
                    throw new RuntimeException("Application " + domain + " failed to start.");
                case STARTED:
                    return;
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                //Quit loop when interrupted
                break;
            }
        }
        throw new RuntimeException("Waited on <"+getDomain()+"> deployment for <"+maxWaitTime+"> ms");
    }

    public final void stop() {
        ClientResponse response = createApplicationBuilder(domain + "/status/").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, new ApplicationStatusChange(ApplicationStatus.stop));
        handleErrors(response);
    }
    

    public final void start(Long maxWaitTime) {
        ClientResponse response = createApplicationBuilder(domain + "/status/").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, new ApplicationStatusChange(ApplicationStatus.start));
        handleErrors(response);
        
        waitForStart(maxWaitTime);
    }

    public void delete() {
        ClientResponse response = createApplicationBuilder(domain).type(MediaType.APPLICATION_JSON_TYPE).delete(ClientResponse.class);
        handleErrors(response);
    }

    protected final WebResource.Builder createApplicationBuilder(final String path) {
    	return createBuilder("applications/" + path);
        
    }

    public Application get() {
        ClientResponse response = createApplicationBuilder(domain).get(ClientResponse.class);
        handleErrors(response);
        
        return response.getEntity(Application.class);
    }
    

}