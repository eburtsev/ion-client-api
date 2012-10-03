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

import java.util.Map;

/**
 * Maps application JSON type.
 *
 * @see http://www.mulesoft.org/documentation/display/CLOUDHUB/Create+Application
 */

public class ApplicationCreateInfo {
	    private String domain;
	    private Integer workers;
	    private String muleVersion;
	    private Map<String,String> properties;

	    public ApplicationCreateInfo(Application application) {
	    	 	setDomain(application.getDomain());
		        setWorkers(application.getWorkers());
		        setMuleVersion(application.getMuleVersion());
		        setProperties(application.getProperties());
	    }

	    public ApplicationCreateInfo(String p_domain, Integer p_workers, String p_muleVersion, Map<String, String> p_properties)  {
	        setDomain(p_domain);
	        setWorkers(p_workers);
	        setMuleVersion(p_muleVersion);
	        setProperties(p_properties);
	    }

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public Integer getWorkers() {
			return workers;
		}

		public void setWorkers(Integer workers) {
			this.workers = workers;
		}

		public String getMuleVersion() {
			return muleVersion;
		}

		public void setMuleVersion(String muleVersion) {
			this.muleVersion = muleVersion;
		}

		public Map<String,String> getProperties() {
			return properties;
		}

		public void setProperties(Map<String,String> properties) {
			this.properties = properties;
		}
}
