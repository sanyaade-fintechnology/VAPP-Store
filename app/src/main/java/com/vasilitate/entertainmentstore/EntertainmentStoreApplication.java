package com.vasilitate.entertainmentstore;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vasilitate.vapp.sdk.Vapp;
import com.vasilitate.vapp.sdk.VappNumberRange;
import com.vasilitate.vapp.sdk.VappProduct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EntertainmentStoreApplication extends Application {

    /**
     * The SDK Key provided by VAPP! that identifies your application
     */
    // FIXME - Please substitue your app's VAPP! Id here.
    private static final String VAPP_SDK_KEY = "A1EE9CB28A54C87C2539";
    private static final boolean TEST_MODE = false;       // Test Mode
    private static final boolean CANCELLABLE_PRODUCTS = true; // Cancellable Products?

    private static Context context;

    @Override public void onCreate() {
        super.onCreate();
        context = this;

        /**
         * You could simply create your down static 'productList' list in code here.  The example
         * shows how this list could be read in as JSON...
         */

        // load all possible Store Items into list
        List<StoreItem> storeItemList = new ArrayList<>();
        String[] testData = {"book_items.json", "music_items.json"};

        for (String filePath : testData) {
            storeItemList.addAll(getTestData(filePath));
        }

        // convert the test data items into VAPP! products
        List<VappProduct> productList = new ArrayList<>();

        for (StoreItem storeItem : storeItemList) {
            productList.add(storeItem.generateVappProduct());
        }

        try {
            // initialise VAPP! with the products
            Vapp.initialise(this,
                    productList,
                    TEST_MODE,
                    CANCELLABLE_PRODUCTS,
                    VAPP_SDK_KEY);

        } catch( Exception e ) {

            Toast.makeText( this, "Vapp Exception: " + e.getMessage(), Toast.LENGTH_LONG ).show();
        }
    }


    /**
     * Deserialises test data for a store listing from JSON - in a real app this would likely come
     * from a 3rd party REST API.
     *
     * @return fake test data
     */
    public static List<StoreItem> getTestData(String filename) {
        List<StoreItem> itemList = new ArrayList<>();

        try { // read file from assets into string, then serialise using GSON library
            InputStream is = context.getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder json = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            StoreListing storeListing = new Gson().fromJson(json.toString(), StoreListing.class);
            return storeListing.getStoreItems();
        }
        catch (IOException e) {
            Log.e("EntertainmentStore", "Error reading JSON data", e);
        }
        return itemList;
    }
}
