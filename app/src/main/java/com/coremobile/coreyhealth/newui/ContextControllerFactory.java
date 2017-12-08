package com.coremobile.coreyhealth.newui;

import android.app.Activity;

import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.LocalPrefs;
import com.coremobile.coreyhealth.MessageItem;
import com.coremobile.coreyhealth.R;

import java.util.ArrayList;

public class ContextControllerFactory {
    public static IContextController getContextController(
        Activity activity, ContextData coreyCxt, ArrayList<MessageItem> messages) {
        IContextAction contextAction = getContextAction(activity, coreyCxt);
        if (messages != null)
            return new ContextController(activity, R.layout.msg_list_adapterlayout,
                messages, coreyCxt, contextAction);
        else
            return new NoItemsController(activity, coreyCxt, contextAction);
    }

    public static IContextAction getContextAction(Activity activity, ContextData coreyCxt) {
        boolean isHealthApp = LocalPrefs.INSTANCE.autoSync();

        // for health app, no location, todo sync

        if (coreyCxt.mName.equals("Schedule")) {
            return new ScheduleSync(activity, coreyCxt);
        } else if (!isHealthApp && coreyCxt.mName.equals("Location")) {
            return new LocationSync(activity, coreyCxt);
        } else if (!isHealthApp && coreyCxt.mName.equals("ToDo")) {
            return new TodoSync(activity, coreyCxt);
        } else if (coreyCxt.mName.equals("Coreyfy")) {
            return new CoreyfySync(activity, coreyCxt);
        } else {
            return null;
        }
    }
}
