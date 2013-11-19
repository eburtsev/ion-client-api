package com.mulesoft.cloudhub.client;

import java.util.Date;
import java.util.Map;

/**
 * @author mariano.gonzalez@mulesoft.com
 */
public class Tenant
{

    private String id;
    private String href;
    private Date created;
    @Deprecated
    private String companyName;
    @Deprecated
    private String contactName;
    @Deprecated
    private String contactEmail;

    private String name;
    private String email;

    private Boolean enabled = Boolean.FALSE;

    private Map<String, String> configuration;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getHref()
    {
        return href;
    }

    public void setHref(String href)
    {
        this.href = href;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    @Deprecated
    public String getCompanyName()
    {
        return name;
    }

    @Deprecated
    public void setCompanyName(String companyName)
    {
        this.name = companyName;
    }

    @Deprecated
    public String getContactName()
    {
        return "";
    }

    @Deprecated
    public void setContactName(String contactName)
    {

    }

    @Deprecated
    public String getContactEmail()
    {
        return email;
    }

    @Deprecated
    public void setContactEmail(String contactEmail)
    {
        this.email = contactEmail;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        if ( enabled == null )
        {
            enabled = Boolean.FALSE;
        }

        this.enabled = enabled;
    }

    public Map<String, String> getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(Map<String, String> configuration)
    {
        this.configuration = configuration;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Override
    public String toString()
    {
        return "Tenant{" +
               "id='" + id + '\'' +
               ", href='" + href + '\'' +
               ", created=" + created +
               ", companyName='" + companyName + '\'' +
               ", contactName='" + contactName + '\'' +
               ", contactEmail='" + contactEmail + '\'' +
               ", enabled=" + enabled +
               ", configuration=" + configuration +
               '}';
    }
}
