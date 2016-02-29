package nu.geeks.uio_kth.Views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nu.geeks.uio_kth.R;

/**
 * Created by hannespa on 16-02-23.
 */
public class PopupViews {

    public static void ShareView(Activity context, Typeface font, String projectID){

        final Activity _context = context;

        LayoutInflater inflater = context.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.share_view, null);
        final AlertDialog builder = new AlertDialog.Builder(context).create();
        builder.setView(dialogLayout);
        final String id = projectID;
        Button ok = (Button) dialogLayout.findViewById(R.id.bDoneShare);
        Button sms = (Button) dialogLayout.findViewById(R.id.bt_share_sms);
        Button mail = (Button) dialogLayout.findViewById(R.id.bt_share_mail);
        TextView text = (TextView)dialogLayout.findViewById(R.id.tv_share_text);
        text.setTypeface(font);
        text.setTextColor(Color.WHITE);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupViews.shareSms(id, _context);
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupViews.shareEmail(id, _context);
            }
        });

        builder.show();
    }

    private static void shareEmail(String id, Activity context){
        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "You have been invited to a UIO Project");
        intent.putExtra(Intent.EXTRA_TEXT, "You have been added to a project on Uio! Click here: https://u.io/?id=" + id);
        intent.setData(Uri.parse("mailto:")); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        context.startActivity(intent);
    }

    private static void shareSms(String id, Activity context) {
        if(!id.equals("null")){
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("sms_body", "You have been added to a project on Uio! Click here: https://u.io/?id=" + id);
            sendIntent.setType("vnd.android-dir/mms-sms");
            context.startActivity(sendIntent);
        }
    }


    public static void PersonalExpensesView(Activity context, Typeface font, String msg){
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.single_person_transactions, null);
        final AlertDialog builder = new AlertDialog.Builder(context).create();
        builder.setView(dialogLayout);

        Button ok = (Button) dialogLayout.findViewById(R.id.tv_single_person_ok);
        TextView text = (TextView)dialogLayout.findViewById(R.id.tv_single_person_transactions);
        text.setTypeface(font);
        text.setTextColor(Color.WHITE);
        text.setText(msg);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.show();
    }

}
