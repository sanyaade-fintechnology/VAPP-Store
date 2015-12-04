package com.vasilitate.entertainmentstore;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StoreListing {

    @SerializedName("store_items")
    private List<StoreItem> storeItems;

    public List<StoreItem> getStoreItems() {
        return storeItems;
    }
}
