package com.vasilitate.entertainmentstore;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vasilitate.vapp.sdk.Vapp;
import com.vasilitate.vapp.sdk.VappProduct;
import com.vasilitate.vapp.sdk.VappProgressReceiver;
import com.vasilitate.vapp.sdk.VappProgressWidget;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Displays a list of items in a store category which can be purchased/interacted with by item clicks
 */
public class StoreListFragment extends Fragment {

    private static final String EXTRA_POSITION = "EXTRA_POSITION";

//    private String productIdBeingPurchased = null;

    private VappProgressReceiver smsProgressReceiver;

    private StoreItemAdapter listAdapter;

    private boolean musicStore; // either book or music store, depending on position

    public static StoreListFragment newInstance(int position) {
        StoreListFragment storeListFragment = new StoreListFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_POSITION, position);

        storeListFragment.setArguments(args);
        return storeListFragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if (args != null) {
            musicStore = args.getInt(EXTRA_POSITION, 0) == 1;
        }

        smsProgressReceiver = new VappProgressReceiver(this.getActivity(),
                                                       null ); // listener will be set later

        // All onCreate to hook up the listener but don't forget to call onDestroy() later!
        smsProgressReceiver.onCreate();

//        VappProduct productBeingPurchased = Vapp.getProductBeingPurchased(getActivity());
//        if( productBeingPurchased != null ) {
//            productIdBeingPurchased = productBeingPurchased.getProductId();
//        }
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_list, container, false);
        ListView listView = (ListView) view;

        String filename = musicStore ? "music_items.json" : "book_items.json";
        List<StoreItem> testData = EntertainmentStoreApplication.getTestData(filename);
        listAdapter = new StoreItemAdapter(getActivity(), testData, musicStore);
        listView.setAdapter(listAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Call onDestroy on the receiver to allow it to unregister its contained receiver...
        if( smsProgressReceiver != null ) {
            smsProgressReceiver.onDestroy();
            smsProgressReceiver = null;
        }
    }


    /**
     * Copies a file from the app's asset folder to External Storage, allowing the user to access
     * the content. This scheme can be replaced with however you wish to control content
     *
     * @param context the context
     * @param filename the filename
     */
    private static void copyAssetToExternalStorage(Context context, String filename) {
        AssetManager assets = context.getAssets();
        File copy = new File(context.getFilesDir(), filename);
        InputStream is;
        OutputStream os;

        try {
            is = assets.open(filename);
            os = context.openFileOutput(copy.getName(), Context.MODE_WORLD_READABLE);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }

            os.flush();
            os.close();
            is.close();
        }
        catch (IOException e) {
            Log.e("StoreListFragment", "Error copying PDF to external storage", e);
        }
    }

//    @Override
//    public void onSMSSent(int progress, int currentInterval, int countDown) {
//
//        urchaseSMSProgress = progress;
//        purchaseSMSInterval = currentInterval;
//        purchaseSMSIntervalProgress = 0;
//
//        this.countDown = countDown;
//
//        listAdapter.notifyDataSetChanged();
//    }
//
//
//    @Override
//    public void onProgressTick(int countDown) {
//
//        this.countDown = countDown;
//        purchaseSMSIntervalProgress++;
//
//        listAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onError(String message) {
//
//        // There's been a problem with the VAPP! setup - display the problem and then exit.
//        android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(getActivity());
//        alertBuilder.setMessage(message)
//                .setTitle("VAPP! Error")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        productIdBeingPurchased = null;
//                        listAdapter.notifyDataSetChanged();
//                    }
//                });
//    }
//
//    @Override
//    public void onCompletion() {
//        productIdBeingPurchased = null;
//        listAdapter.notifyDataSetChanged();
//    }

    /**
     * Adapter which handles how the items in the store should be displayed on screen in a
     * scrollable list.
     */
    private class StoreItemAdapter extends ArrayAdapter<StoreItem> {

        private final LayoutInflater inflater;
        private final boolean musicStore;

        public StoreItemAdapter(Context context, List<StoreItem> objects, boolean musicStore) {
            super(context, R.layout.list_item_store, objects);
            inflater = LayoutInflater.from(context);
            this.musicStore = musicStore;
        }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            final StoreItem storeItem = getItem(position);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_store, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.label.setText(storeItem.getLabel());
            viewHolder.icon.setImageResource(musicStore ? R.drawable.music_icon : R.drawable.book_icon);

            VappProduct product = storeItem.generateVappProduct();

            boolean isPaidFor = Vapp.isPaidFor(getContext(), product);
            viewHolder.label.setTypeface(null, isPaidFor ? Typeface.BOLD : Typeface.NORMAL);
            viewHolder.label.setTextSize(isPaidFor ? 18.0f : 15.0f);

            VappProduct productBeingPurchased = Vapp.getProductBeingPurchased(getActivity());

            if( productBeingPurchased != null &&
                storeItem.getVappProductId().equals(productBeingPurchased.getProductId()) ) {

                smsProgressReceiver.setListener( viewHolder.smsProgress );
                viewHolder.smsProgress.setVisibility(View.VISIBLE);
                viewHolder.smsProgress.display(product,
                        new VappProgressWidget.VappCompletionListener() {
                            @Override
                            public void onError(String s) { }

                            @Override
                            public void onErrorAcknowledged() { }

                            @Override
                            public void onCompletion() {
                                // We need to reflect the list view after completion to show
                                // the product as now purchased.
                                listAdapter.notifyDataSetChanged();
                            }
                        });
            } else {
                viewHolder.smsProgress.setVisibility(View.GONE);
            }

            viewHolder.root.setOnClickListener(new View.OnClickListener() { // launch the payment activity
                @Override
                public void onClick(View v) {
                    VappProduct product = storeItem.generateVappProduct();

                    if (Vapp.isPaidFor(getContext(), product)) { // view item
                        if (musicStore) {
                            playMusicAsset( storeItem.getFilename() );
                        }
                        else {
                            displayPdfAsset( storeItem.getFilename() );
                        }
                    }
                    // payment is in progress, do nothing
                    else if (Vapp.isBeingPaidFor(getContext(), product)) {

                    }
                    else { // prompt user to purchase item

                        Intent intent = new Intent(getContext(), PaymentActivity.class);
                        intent.putExtra(PaymentActivity.EXTRA_ITEM_STORE_ITEM, storeItem);
                        getContext().startActivity(intent);
                    }
                }
            });
            return convertView;
        }

        private void displayPdfAsset(String filename) { // allow user to view unlocked PDF
            openUnlockedContent(filename, "application/pdf", "No PDF Reader Found", "Please install an app that reads PDFs.");
        }

        private void playMusicAsset(String filename) { // allow user to play unlocked music
            openUnlockedContent(filename, "audio/mpeg", "No Music Player Found", "Please install an app that plays music.");
        }

        /**
         * Opens an unlocked piece of content using 3rd party apps, by copying to external storage.
         * This can be replaced with whatever logic you wish to use in your app.
         *
         * @param filename the filename of the asset
         * @param mimeType the mimetype of the asset
         * @param errTitle the error title if no activity is available to handle the intent
         * @param errMesage the error message if no activity is available to handle the intent
         */
        private void openUnlockedContent(String filename, String mimeType, String errTitle, String errMesage) {
            copyAssetToExternalStorage(getContext(), filename);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "file://" + getContext().getFilesDir() + "/" + filename;
            intent.setDataAndType(Uri.parse(uriString), mimeType);

            try {
                getContext().startActivity(intent);
            }
            catch (ActivityNotFoundException e) {
                new AlertDialog.Builder(getContext())
                        .setTitle(errTitle)
                        .setMessage(errMesage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        }
    }

    /**
     * Improve scroll performance of ListView by caching views
     */
    private static class ViewHolder {

        private final ImageView icon;
        private final TextView label;
        private final View root;
        private final VappProgressWidget smsProgress;

        public ViewHolder(View view) {
            root = view;
            icon = (ImageView) view.findViewById(R.id.store_item_icon);
            label = (TextView) view.findViewById(R.id.store_item_label);
            smsProgress = (VappProgressWidget) view.findViewById(R.id.progress_widget);
        }
    }
}
