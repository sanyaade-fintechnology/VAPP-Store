package com.vasilitate.entertainmentstore;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vasilitate.vapp.sdk.Vapp;

/**
 * Displays a payment screen which allows the user to choose payment method, and initialise the
 * VAPP! Payment
 */
public class PaymentActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_STORE_ITEM = "EXTRA_ITEM_STORE_ITEM";

    private StoreItem storeItem;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        TextView itemTitle = (TextView) findViewById(R.id.store_item_title);

        Button payByVappButton = (Button) findViewById(R.id.btn_pay_vapp);

        payByVappButton.setOnClickListener(vappClickListener);
        findViewById(R.id.btn_pay_credit_card).setOnClickListener(unsupportedClickListener);
        findViewById(R.id.btn_pay_short_code).setOnClickListener(unsupportedClickListener);

        // Disable the Vapp button if another product is currently being bought using VAPP!
        payByVappButton.setEnabled( Vapp.getProductBeingPurchased(this) == null );

        storeItem = (StoreItem) getIntent().getExtras().getSerializable(EXTRA_ITEM_STORE_ITEM);

        if (storeItem != null) {
            itemTitle.setText(storeItem.getLabel());
        }
    }

    /**
     * Prompt the user so that they are aware of how much they will be charged, then use the VAPP!
     * SDK to initialise the payment if they accept.
     */
    private View.OnClickListener vappClickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {

            int smsCount = storeItem.getSmsCount();
            String messageText = getString(R.string.payment_prompt_message, smsCount);

            new AlertDialog.Builder(PaymentActivity.this)
                    .setTitle("Confirm Payment")
                    .setMessage(messageText)
                    .setNegativeButton(android.R.string.cancel, null)

                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {

                        // Initialise payment for product.
                        Vapp.showVappPaymentScreen(PaymentActivity.this, storeItem.generateVappProduct(), false, 0);
                        }
                    })
                    .show();
        }
    };

    /**
     * If a payment is unsupported, display dialog showing to the user
     */
    private View.OnClickListener unsupportedClickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {

            new AlertDialog.Builder(PaymentActivity.this)
                    .setTitle("Payment method unsupported")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    };
}
