package com.coremobile.coreyhealth.nativeassignment;

import java.io.Serializable;

/**
 * Created by nitij on 28-05-2016.
 */

public class AllProvidersModel implements Serializable {

    String providerId, providerName;
    int roleCategory;

    public int getRoleCategory() {
        return roleCategory;
    }

    public void setRoleCategory(int roleCategory) {
        this.roleCategory = roleCategory;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String ProviderId) {
        providerId = ProviderId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String ProviderName) {
        providerName = ProviderName;
    }
}
