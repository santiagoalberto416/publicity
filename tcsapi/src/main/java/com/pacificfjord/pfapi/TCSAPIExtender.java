package com.pacificfjord.pfapi;

import java.util.Map;

/**
 * Created by Aaron Vega on 9/6/16.
 */
public interface TCSAPIExtender {
    Map<String, TCSAPICallDefinition> getTCSAPICallDefinitions();
}
