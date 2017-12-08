package com.coremobile.coreyhealth.newui;

import android.app.Activity;
import android.content.Intent;

import com.coremobile.coreyhealth.AboutActivityCMN;
import com.coremobile.coreyhealth.AddStaffActivityCMN;
import com.coremobile.coreyhealth.AuthenticationActivityCMN;
import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.GeneralSettingsActivityCMN;
import com.coremobile.coreyhealth.PatientAnalyticsActivityCMN;
import com.coremobile.coreyhealth.RatingSelectActivityCMN;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.genericviewandsurvey.CMN_GenericViewActivity;
import com.coremobile.coreyhealth.nativeassignment.AssignmentsActivityCMN;

public class ContextMenuItemFactory {
    public static IContextMenuItem createContextMenuItem(
            Activity activity, ContextData coreyCxt) {
        if (coreyCxt.mName.equalsIgnoreCase("ApplicationSettings")) {
            return makeApplicationsSettingsMenuItem(activity, coreyCxt);
        } else if (coreyCxt.mName.equalsIgnoreCase("GeneralSettings")) {
            return makeGeneralSettingsMenuItem(activity, coreyCxt);
        } else if (coreyCxt.mName.equalsIgnoreCase("About")) {
            return makeAboutMenuItem(activity, coreyCxt);
        } else if (coreyCxt.mName.equalsIgnoreCase("ConfigSync")) {
            return makeConfigSyncMenuItem(activity, coreyCxt);
        } else if (coreyCxt.mName.equalsIgnoreCase("SignOut")) {
            return makeSignoutMenuItem(activity, coreyCxt);
        } else if (coreyCxt.mName.equalsIgnoreCase("Add Provider")) {
            return makeAddProviderMenuItem(activity, coreyCxt);
        } else if (coreyCxt.mName.equalsIgnoreCase("PeriOp Manager")) {
            return makeAnalyticsMenuItem(activity, coreyCxt);
        } else if (coreyCxt.mName.equalsIgnoreCase("Provide Feedback")) {
            return makeFeedbackMenuItem(activity, coreyCxt);
        } else if (coreyCxt.mName.equalsIgnoreCase("Assignments")) {
            return makeAssignmentsMenuItem(activity, coreyCxt);
        } else if (coreyCxt.mName.equalsIgnoreCase("AddProviderNew") || coreyCxt.mName.equalsIgnoreCase("MyProfile") ||coreyCxt.mName.equalsIgnoreCase("SecuritySettings") ) {
            return makeGenericViewMenuItem(activity, coreyCxt);
        } else if (coreyCxt.mURL != null) {
            return makeWebViewMenuItem(activity, coreyCxt);
        } else {
            System.out.println(
                    "ERROR: createContextMenuItem unknown context: " + coreyCxt.mName);
            return null;
        }
    }

    private static IContextMenuItem makeApplicationsSettingsMenuItem(Activity activity, ContextData coreyCxt) {
        return new LauncherContextMenuItem(
                activity, coreyCxt,
                new Intent(activity, AuthenticationActivityCMN.class));
    }

    private static IContextMenuItem makeGeneralSettingsMenuItem(Activity activity, ContextData coreyCxt) {
        return new LauncherContextMenuItem(
                activity, coreyCxt,
                new Intent(activity, GeneralSettingsActivityCMN.class));
    }

    private static IContextMenuItem makeAboutMenuItem(Activity activity, ContextData coreyCxt) {
        return new LauncherContextMenuItem(
                activity, coreyCxt,
                new Intent(activity, AboutActivityCMN.class));
    }

    private static IContextMenuItem makeAddProviderMenuItem(Activity activity, ContextData coreyCxt) {
        return new LauncherContextMenuItem(
                activity, coreyCxt,
                new Intent(activity, AddStaffActivityCMN.class));
    }

    private static IContextMenuItem makeAnalyticsMenuItem(Activity activity, ContextData coreyCxt) {
        return new LauncherContextMenuItem(
                activity, coreyCxt,
                new Intent(activity, PatientAnalyticsActivityCMN.class));
    }

    private static IContextMenuItem makeFeedbackMenuItem(Activity activity, ContextData coreyCxt) {
        return new LauncherContextMenuItem(
                activity, coreyCxt,
                new Intent(activity, RatingSelectActivityCMN.class));
    }

    private static IContextMenuItem makeAssignmentsMenuItem(Activity activity, ContextData coreyCxt) {
        return new LauncherContextMenuItem(
                activity, coreyCxt,
                new Intent(activity, AssignmentsActivityCMN.class));
    }

    private static IContextMenuItem makeGenericViewMenuItem(Activity activity, ContextData coreyCxt) {
        return new LauncherContextMenuItem(
                activity, coreyCxt,
                new Intent(activity, CMN_GenericViewActivity.class).putExtra("data", coreyCxt));
    }

    private static IContextMenuItem makeConfigSyncMenuItem(Activity activity, ContextData coreyCxt) {
        return new ConfigSyncContextMenuItem(activity, coreyCxt);
    }

    private static IContextMenuItem makeSignoutMenuItem(Activity activity, ContextData coreyCxt) {
        return new SignoutContextMenuItem(activity, coreyCxt);
    }

    private static IContextMenuItem makeWebViewMenuItem(Activity activity, ContextData coreyCxt) {
        return new LauncherContextMenuItem(
                activity, coreyCxt,
                Utils.makeWebViewIntent(activity, coreyCxt));
    }
}
