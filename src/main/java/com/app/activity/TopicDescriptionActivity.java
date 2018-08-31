package com.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.constants.GeenieConstants;
import com.app.geenie.R;


public class TopicDescriptionActivity extends Activity {

    TextView topic, shortDesc, detailedDesc;
   /* Intent intent = getIntent();*/
    String userName = null;
    LinearLayout linearLayout;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_topic_description);

        topic = (TextView)findViewById(R.id.selectedTopicName);
        shortDesc = (TextView)findViewById(R.id.shortDescriptionView);
        detailedDesc = (TextView)findViewById(R.id.detailedDescriptionView);
        Button chat = (Button)findViewById(R.id.chatNow);
        linearLayout = (LinearLayout)findViewById(R.id.mainLinearLayout);

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String type = extras.getString("type");
        if(type.equals("service")){
            //String topicSelected = i.getStringExtra("topic");
            String message = i.getStringExtra("message");

            String[] params;
            String delimiter = ",";
            params = message.split(delimiter);

            if(params[1].equals("Travel")){
                linearLayout.setBackground(getResources().getDrawable(R.drawable.travel));
            }
            if(params[1].equals("Shopping")){
                linearLayout.setBackground(getResources().getDrawable(R.drawable.shopping));
            }
            if(params[1].equals("Food")){
                linearLayout.setBackground(getResources().getDrawable(R.drawable.food));
            }

            userName = params[0];
            topic.setText(params[1]);
            shortDesc.setText(params[2]);
            detailedDesc.setText(params[3]);
        }

        if(type.equals("searchResult")) {
            populateValues(i.getStringExtra("topic"), i.getStringExtra("shortDesc"), i.getStringExtra("longDesc"), i.getStringExtra("userName"));
        }

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    pd = new ProgressDialog(TopicDescriptionActivity.this);
                    pd.setTitle("Processing...");
                    pd.setMessage("Please wait.");
                    pd.setCancelable(false);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.show();

                    Intent i = new Intent(TopicDescriptionActivity.this, ChatActivity.class);
                    i.putExtra("type", "wishDetails");
                    i.putExtra("TOUSER", userName);
                    startActivity(i);
                    finish();
                }
                else {
                    Toast.makeText(TopicDescriptionActivity.this, GeenieConstants.INTERNET_UNAVAILABLE, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(TopicDescriptionActivity.this, UserActivity.class);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    public void populateValues(String topicOfInterest, String shortDescOfInterest, String longDescOfInterest, String user){

        topic.setText(topicOfInterest);
        shortDesc.setText(shortDescOfInterest);
        detailedDesc.setText(longDescOfInterest);
        userName = user;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifi.isConnected() || mobile.isConnected());
    }
}
