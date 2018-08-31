package com.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.constants.GeenieConstants;
import com.app.geenie.R;
import com.app.helper.UserBean;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.app.constants.GeenieConstants.PARSE_PASSWORD;
import static com.app.constants.GeenieConstants.PARSE_USERNAME;


public class SearchResultActivity extends Activity implements OnItemClickListener{

    private ListView lv, lv1;
    private List<UserBean> users = new ArrayList<>();
    List<ParseObject> ob;
    String category;
    ArrayList<String> userIDList = new ArrayList<>();
    ArrayList<String> shortDescriptionList = new ArrayList<>();
    ArrayList<String> detailedDescriptionList = new ArrayList<>();
    String [] topics, shortDescriptions, detailedDescriptions, usersName;
    LinearLayout linearLayout;
    private ProgressDialog pd;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        lv = (ListView) findViewById(R.id.listView);
        lv1 = (ListView) findViewById(R.id.listView1);
        linearLayout = (LinearLayout)findViewById(R.id.mainLinearLayout);

        Parse.initialize(this, PARSE_USERNAME, PARSE_PASSWORD);

        Intent intent = getIntent();
        category = intent.getStringExtra("selectedCategory");
        new FetchTask(this).execute();

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isNetworkAvailable()) {
                    Intent intent1 = new Intent(SearchResultActivity.this, TopicDescriptionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "searchResult");
                    bundle.putString("topic", topics[position]);
                    bundle.putString("shortDesc", shortDescriptions[position]);
                    bundle.putString("longDesc", detailedDescriptions[position]);
                    bundle.putString("userName", usersName[position]);
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                }
                else{
                    Toast.makeText(SearchResultActivity.this, GeenieConstants.INTERNET_UNAVAILABLE, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(SearchResultActivity.this, UserActivity.class);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent1 = new Intent(SearchResultActivity.this, TopicDescriptionActivity.class);
        intent1.putExtra("type", "searchResult");
        startActivity(intent1);
    }

    class VersionAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public VersionAdapter(SearchResultActivity activity) {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View listItem = convertView;
            int pos = position;
            if (listItem == null) {
                listItem = layoutInflater.inflate(R.layout.list_item, null);
            }

            TextView tvTitle = (TextView) listItem.findViewById(R.id.title);
            TextView tvDesc = (TextView) listItem.findViewById(R.id.desc);

            tvTitle.setText(topics[pos]);
            tvDesc.setText(shortDescriptions[pos]);
            return listItem;
        }
    }

    private class FetchTask extends AsyncTask<Void, Void, List<UserBean>> {

        private final SearchResultActivity resultActivity;
        public FetchTask(SearchResultActivity searchResultActivity) {
            resultActivity = searchResultActivity;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(SearchResultActivity.this);
            pd.setTitle("Processing...");
            pd.setMessage("Please wait.");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }
        @Override
        protected List<UserBean> doInBackground(Void... params) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("GenieUser");

            try {

                Date twentyMinutesBack = new Date(System.currentTimeMillis() - 20 * 60 * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                String timeThen = sdf.format(twentyMinutesBack);

                query.whereGreaterThanOrEqualTo("CREATE_DTTM", timeThen);
                query.whereEqualTo("TOPIC", category);
                ob = query.find();
            } catch (ParseException pe) {
                pe.printStackTrace();
            }

            for (ParseObject user : ob) {
                UserBean userBean = new UserBean(user.getString("USER_ID"), user.getString("USER_NAME"), user.getString("CREATE_DTTM"),
                        user.getString("LOCATION_LATITUDE"), user.getString("LOCATION_LONGITUDE"), user.getString("TOPIC"),
                        user.getString("SHORT_DESCRIPTION"), user.getString("LONG_DESCRIPTION"));
                users.add(userBean);
            }

            return users;
        }

        @Override
        protected void onPostExecute(List<UserBean> users) {
            if (pd!=null) {
                pd.dismiss();
                topics = new String[users.size()];
                shortDescriptions = new String[users.size()];
                detailedDescriptions = new String[users.size()];
                usersName = new String [users.size()];

                for (int i = 0; i < users.size(); i++) {
                    topics[i] = users.get(i).getTopic();
                    shortDescriptions[i] = users.get(i).getBrief();
                    detailedDescriptions[i] = users.get(i).getDetail();
                    usersName[i] = users.get(i).getUserName();
                }
                userIDList.addAll(Arrays.asList(topics));
                shortDescriptionList.addAll(Arrays.asList(shortDescriptions));
                detailedDescriptionList.addAll(Arrays.asList(detailedDescriptions));
                lv.setAdapter(new VersionAdapter(SearchResultActivity.this));

            }

        }
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