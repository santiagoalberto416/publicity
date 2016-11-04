package com.pacificfjord.pfapi;

/**
 * Created by tom on 16/01/14.
 */

/**
 API call definition
 These are created and added to an instance of TCSAPIConstants
 */
public class TCSAPICallDefinition {
    public String versionPath;/**<version path of the definition */
    public String apiPath;/**< The relative API path of the API call*/
    public TCSAPIConstants.TCSRequestMethod method;/**<The request method of the call */

    /**
     Create with version path, path and method
     @param versionPath the version path of the API call
     @param apiPath the path of the API call
     @param method the method of the API call
     */
    public TCSAPICallDefinition(String versionPath, String apiPath, TCSAPIConstants.TCSRequestMethod method)
    {
        this .versionPath = versionPath;
        this.apiPath = apiPath;
        this.method = method;
    }
}
