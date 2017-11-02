package de.hampager.dapnetmobile.fragments;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

/**
 * Created by schwarz on 27.10.17.
 */


public class TransmitterOverlayItem extends OverlayItem {
    private boolean wideRangeUsage;
    private boolean online;

    public TransmitterOverlayItem(String aTitle, String aSnippet, IGeoPoint aGeoPoint,boolean wideRangeUsage, boolean online) {
        super(aTitle, aSnippet, aGeoPoint);
        this.wideRangeUsage=wideRangeUsage;
        this.online=online;
    }
}
