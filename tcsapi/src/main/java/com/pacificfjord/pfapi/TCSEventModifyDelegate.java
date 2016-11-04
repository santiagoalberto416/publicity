package com.pacificfjord.pfapi;

/**
 * Created by tom on 14/08/14.
 */

/**
 A delegate to be given an opportunity to modify the event before submission
 */
public interface TCSEventModifyDelegate{
    /**
     Notifies the delegate that the event will be submitted
     Passed by reference so that changes can be made such as adding a parameter or metadata.
     @param event event that is about to be submitted.
     */
    public void eventWillBeSubmitted(TCSEvent event);
}
