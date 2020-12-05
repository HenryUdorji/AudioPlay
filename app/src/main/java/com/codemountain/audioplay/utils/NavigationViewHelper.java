package com.codemountain.audioplay.utils;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.codemountain.audioplay.MainActivity;
import com.codemountain.audioplay.R;
import com.codemountain.audioplay.activities.SettingsActivity;
import com.google.android.material.navigation.NavigationView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class NavigationViewHelper {


    public static void enableNavigation(final Context context, final NavigationView view){
        view.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_rate_app:
                    try{
                        Uri rateApp = Uri.parse("market://details?id=" + context.getPackageName());
                        Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, rateApp);
                        context.startActivity(rateAppIntent);
                    }
                    catch (ActivityNotFoundException e) {
                        Uri rateApp = Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName());
                        Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, rateApp);
                        context.startActivity(rateAppIntent);
                    }
                    return true;

                case R.id.nav_settings:
                    context.startActivity(new Intent(context, SettingsActivity.class));
                    ((MainActivity)context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    return true;

                case R.id.nav_about:
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.about_dialog);
                    ImageButton closeBtn = dialog.findViewById(R.id.dialogCloseBtn);
                    ImageView linkedInBtn = dialog.findViewById(R.id.dialogLinkedInBtn);
                    ImageView twitterBtn = dialog.findViewById(R.id.dialogTwitterBtn);
                    ImageView whatsAppBtn = dialog.findViewById(R.id.dialogWhatsAppBtn);

                    dialog.setCanceledOnTouchOutside(true);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    closeBtn.setOnClickListener(v -> dialog.dismiss());
                    linkedInBtn.setOnClickListener(v -> {
                                try {
                                    String url = "linkedin://profile/henry-udorji-98204a1b1";
                                    context.getPackageManager().getPackageInfo("com.linkedin.android", 0);
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                    Uri url = Uri.parse("http://www.linkedin.com/in/henry-udorji-98204a1b1" + context.getPackageName());
                                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                                    context.startActivity(intent);
                                }

                            }
                    );
                    twitterBtn.setOnClickListener(v -> {
                        String username = "@henry_ifechukwu";
                                try{
                                    String uri = "twitter://user?screen_name=" + username;
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                                }
                                catch (ActivityNotFoundException e) {
                                    String uri = "https://twitter.com/#!/" + username;
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                                }
                    }
                    );
                    whatsAppBtn.setOnClickListener(v -> {
                                PackageManager manager = context.getPackageManager();
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                try{
                                    String url = "https://api.whatsapp.com/send?phone="+ "+2347062396742" +"&text=" + URLEncoder.encode("Hy developer!", "UTF-8");
                                    intent.setPackage("com.whatsapp");
                                    intent.setData(Uri.parse(url));
                                    if (intent.resolveActivity(manager) != null){
                                        context.startActivity(intent);
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            }
                    );
                    return true;

                case R.id.nav_feedback:
                    final Intent feedBackIntent = new Intent(Intent.ACTION_SEND);
                    feedBackIntent.setType("message/rfc822");
                    feedBackIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.feedback_email)});
                    feedBackIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_subject));
                    feedBackIntent.setPackage("com.google.android.gm");
                    context.startActivity(feedBackIntent);
                    return true;

            }
            return false;
        });
    }
}
