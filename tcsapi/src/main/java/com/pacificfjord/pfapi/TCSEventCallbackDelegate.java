package com.pacificfjord.pfapi;

import java.util.List;

/**
 * Created by mind-p6 on 12/4/14.
 */
public interface TCSEventCallbackDelegate {

    public void done(boolean success, List<TCSAppAction> actions);
}
