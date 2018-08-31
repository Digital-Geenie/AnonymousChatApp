package com.app.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.app.geenie.R;
import com.app.helper.ObjectDrawerItem;
import com.app.navigationdrawer.DrawerItemCustomAdapter;
import com.app.services.MQTTservice;

import static com.app.geenie.R.*;


public class UserActivity extends ActionBarActivity {

    private String[] options = {"Search", "Create", "Subscribe"};
    private String[] menuOptions;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private CharSequence title;
    private IBinder svcbinder = null;
    private Messenger service = null;
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_user);

        ListView listView = (ListView)findViewById(id.list1);
        listView.setAdapter(new VersionAdapter(this));

        menuOptions = getResources().getStringArray(array.nav_drawer_items);
        drawerLayout = (DrawerLayout)findViewById(id.drawer_layout);
        drawerList = (ListView)findViewById(id.left_drawer);

        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[2];

        drawerItem[0] = new ObjectDrawerItem(drawable.home_icon, "Home");
        drawerItem[1] = new ObjectDrawerItem(drawable.subscriptions, "Subscriptions");

        title = drawerTitle = getTitle();

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, layout.listview_item_row, drawerItem);
        drawerList.setAdapter(adapter);

        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        startService(new Intent(this, MQTTservice.class));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (position == 0) {
                        pd = new ProgressDialog(UserActivity.this);
                        pd.setTitle("Processing...");
                        pd.setMessage("Please wait.");
                        pd.setCancelable(false);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.show();
                        Intent intent = new Intent(UserActivity.this, SearchActivity.class);
                        startActivity(intent);

                    }
                    if (position == 1) {
                        pd = new ProgressDialog(UserActivity.this);
                        pd.setTitle("Processing...");
                        pd.setMessage("Please wait.");
                        pd.setCancelable(false);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.show();
                        Intent intent = new Intent(UserActivity.this, NewWishActivity.class);
                        Bundle param = new Bundle();
                        param.putBinder("binder",svcbinder);
                        intent.putExtras(param);
                        startActivity(intent);
                    }
                    if (position == 2) {
                        pd = new ProgressDialog(UserActivity.this);
                        pd.setTitle("Processing...");
                        pd.setMessage("Please wait.");
                        pd.setCancelable(false);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.show();
                        Intent intent = new Intent(UserActivity.this, SubscriptionActivity.class);
                        Bundle param = new Bundle();
                        param.putBinder("binder",svcbinder);
                        intent.putExtras(param);
                        startActivity(intent);
                    }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, drawable.ic_drawer, string.drawer_open,
                string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(title);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(drawerTitle);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        bindService(new Intent(UserActivity.this, MQTTservice.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //registerReceiver(pushReceiver, intentFilter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //unregisterReceiver(pushReceiver);
    }

    class VersionAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public VersionAdapter(UserActivity activity) {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return options.length;
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
            if (listItem == null) {
                listItem = layoutInflater.inflate(layout.list_item_layout, null);
            }

            TextView option = (TextView) listItem.findViewById(id.option);

            option.setText(options[position]);

            return listItem;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {

        Intent intentToNavigate = null;

        switch (position) {
            case 0:
                intentToNavigate = new Intent(UserActivity.this, UserActivity.class);
                break;
            case 1:
                intentToNavigate = new Intent(UserActivity.this, CurrentSubscriptionsActivity.class);
                break;
            case 2:
                break;
            default:
                break;
        }

        if (intentToNavigate != null) {

            startActivity(intentToNavigate);

            drawerList.setItemChecked(position, true);
            drawerList.setSelection(position);
            getSupportActionBar().setTitle(menuOptions[position]);
            drawerLayout.closeDrawer(drawerList);

        } else {
            Log.e("UserActivity", "Error in creating fragment");
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder)
        {
            svcbinder = binder;
            service = new Messenger(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
        }
    };
}
