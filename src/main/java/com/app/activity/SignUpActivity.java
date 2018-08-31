package com.app.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Toast;

import com.app.constants.GeenieConstants;
import com.app.geenie.R;
import com.app.services.RegistrationIntentService;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import static com.app.constants.GeenieConstants.RC_SIGN_IN;

public class SignUpActivity extends FragmentActivity implements OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private int dotsCount;
    private TextView[] dots;
    private ViewPager myPager;
    private MyViewPagerAdapter adapter;
    private SignInButton btnSignIn;
    private TextView txtName, txtEmail;
    private LinearLayout llProfileLayout;
    private ImageView imgProfilePic;
    private int imageArray[] = {R.drawable.background, R.drawable.background, R.drawable.background, R.drawable.background};
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);

        btnSignIn.setOnClickListener(this);

        initViews();
        setViewPagerItemsWithAdapter();
        setUiPageViewController();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
         public void onStart() {
         super.onStart();

            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                 if (opr.isDone()) {
                     // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                     // and the GoogleSignInResult will be available instantly.
                     GoogleSignInResult result = opr.get();
                     handleSignInResult(result);
                     }
                 else {
                         // If the user has not previously signed in on this device or the sign-in has expired,
                         // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                         // single sign-on will occur in this branch.

                         opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {

            @Override
            public void onResult(GoogleSignInResult googleSignInResult) {
                handleSignInResult(googleSignInResult);
            }});
         }
    }


    protected void onStop() {
        super.onStop();
        }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
          //  llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        updateUI(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                if(isNetworkAvailable()) {
                    pd = new ProgressDialog(SignUpActivity.this);
                    pd.setTitle("Processing...");
                    pd.setMessage("Please wait.");
                    pd.setCancelable(false);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.show();

                    signIn();
                }
                else{
                    Toast.makeText(SignUpActivity.this, GeenieConstants.INTERNET_UNAVAILABLE, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String personEmail = acct.getEmail();

            updateUI(true);
            Intent intent = new Intent(SignUpActivity.this, RegistrationIntentService.class);
            intent.putExtra("userName", personEmail);
            startService(intent);
        } else {
            updateUI(false);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void setUiPageViewController() {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        dotsCount = adapter.getCount();
        dots = new TextView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(30);
            dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
            dotsLayout.addView(dots[i]);
        }

        dots[0].setTextColor(getResources().getColor(R.color.black));
    }

    private void setViewPagerItemsWithAdapter() {
        adapter = new MyViewPagerAdapter(imageArray);
        myPager.setAdapter(adapter);
        myPager.setCurrentItem(0);
        myPager.setOnPageChangeListener(viewPagerPageChangeListener);
    }

    //	page change listener
    OnPageChangeListener viewPagerPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < dotsCount; i++) {
                dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
            dots[position].setTextColor(getResources().getColor(R.color.red));
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void initViews() {
        myPager = (ViewPager)findViewById(R.id.myfivepanelpager);
        imageArray[0] = R.drawable.background;
        imageArray[1] = R.drawable.background;
        imageArray[2] = R.drawable.background;
        imageArray[3] = R.drawable.background;
    }

    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;
        private int[] items;

        public MyViewPagerAdapter(int[] imageArray) {
            this.items = imageArray;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.view_pager_item, container,false);

            ImageView iView = (ImageView)view.findViewById(R.id.PageNumber);
            iView.setImageResource(imageArray[position]);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View)object;
            container.removeView(view);
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

