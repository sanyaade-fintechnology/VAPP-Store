package com.vasilitate.entertainmentstore;

import com.google.gson.annotations.SerializedName;
import com.vasilitate.vapp.sdk.VappProduct;

import java.io.Serializable;

/**
 * A POJO which models an item that can be bought in a virtual store, such as a book or music track
 */
public class StoreItem implements Serializable {

    @SerializedName("label")
    private final String label;

    @SerializedName("filename")
    private final String filename;

    @SerializedName("id")
    private final String vappProductId; // unique identifying string

    @SerializedName("sms_count")
    private final int smsCount;

    @SerializedName("max_purchase_count")
    private final int maxPurchaseCount;

    public StoreItem(String label, String filename, String vappProductId, int smsCount, int maxPurchaseCount) {
        this.label = label;
        this.filename = filename;
        this.vappProductId = vappProductId;
        this.smsCount = smsCount;
        this.maxPurchaseCount = maxPurchaseCount;
    }

    public String getLabel() {
        return label;
    }

    public String getFilename() {
        return filename;
    }

    public String getVappProductId() {
        return vappProductId;
    }

    public int getSmsCount() {
        return smsCount;
    }

    public int getMaxPurchaseCount() {
        return maxPurchaseCount;
    }

    /**
     * Converts the StoreItem model from your app into a VappProduct, which the SDK can understand.
     *
     * @return a VappProduct instance from the current StoreItem.
     */
    public VappProduct generateVappProduct() {
        return new VappProduct(getVappProductId(), getSmsCount(), getMaxPurchaseCount());
    }
}
