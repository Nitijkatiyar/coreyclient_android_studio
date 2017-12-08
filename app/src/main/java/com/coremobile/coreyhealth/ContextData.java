package com.coremobile.coreyhealth;

import java.io.Serializable;

public class ContextData implements Serializable{
    public String mName;
    public String mDisplayText;
    public String mHelpURL;
    public String mURL;
    public String mMessageType;
    public boolean mNeedsTab;
    public String mTabText;
    public int mPosition;
    public boolean mNeedsGenericView;


    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb
                .append("{\n")
                .append("Name=").append(mName).append("\n")
                .append("DisplayText=").append(mDisplayText).append("\n")
                .append("HelpURL=").append(mHelpURL).append("\n")
                .append("URL=").append(mURL).append("\n")
                .append("MessageType=").append(mMessageType).append("\n")
                .append("NeedsTab=").append(mNeedsTab).append("\n")
                .append("TabText=").append(mTabText).append("\n")
                .append("Position=").append(mPosition).append("\n")
                .append("NeedsGenricView=").append(mNeedsGenericView).append("\n")
                .append("}")
                .toString();
    }

    public boolean isNeedTab() {
        return mNeedsTab;
    }
}
