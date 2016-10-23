package net.appnews.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import net.appnews.Constants;
import net.appnews.R;
import net.appnews.services.NotificationUtils;
import net.appnews.ui.base.BaseActivity;
import net.appnews.ui.fragment.NewsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static int navItemIndex = 0;

    private static final String TAG_ONE = "one";
    private static final String TAG_TWO = "two";
    private static final String TAG_THREE = "three";
    private static final String TAG_FOUR = "four";
    public static String CURRENT_TAG = TAG_ONE;

    private String[] activityTitles;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_ONE;
            loadNewsOneFragment();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "News: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void loadNewsOneFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        // set toolbar title
        setToolbarTitle();
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                NewsFragment newsFragment = NewsFragment.newInstance(getApplicationContext(), 0);
                return newsFragment;
            case 1:
                NewsFragment societyFragment = NewsFragment.newInstance(getApplicationContext(), 1);
                return societyFragment;
            case 2:
                NewsFragment businessFragment = NewsFragment.newInstance(getApplicationContext(), 2);
                return businessFragment;
            case 3:
                NewsFragment healthFragment = NewsFragment.newInstance(getApplicationContext(), 3);
                return healthFragment;
            case 4:
                NewsFragment eduFragment = NewsFragment.newInstance(getApplicationContext(), 4);
                return eduFragment;
            case 5:
                NewsFragment artsFragment = NewsFragment.newInstance(getApplicationContext(), 5);
                return artsFragment;
            case 6:
                NewsFragment sportsFragment = NewsFragment.newInstance(getApplicationContext(), 6);
                return sportsFragment;
            case 7:
                NewsFragment worldFragment = NewsFragment.newInstance(getApplicationContext(), 7);
                return worldFragment;
            case 8:
                NewsFragment techFragment = NewsFragment.newInstance(getApplicationContext(), 8);
                return techFragment;
            case 9:
                NewsFragment travelFragment = NewsFragment.newInstance(getApplicationContext(), 9);
                return travelFragment;
            default:
                return new NewsFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_ONE;
                        break;
                    case R.id.nav_society:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_TWO;
                        break;
                    case R.id.nav_business:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_THREE;
                        break;
                    case R.id.nav_health:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_FOUR;
                        break;
                    case R.id.nav_edu:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_FOUR;
                        break;
                    case R.id.nav_arts:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_FOUR;
                        break;
                    case R.id.nav_sports:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_FOUR;
                        break;
                    case R.id.nav_world:
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_FOUR;
                        break;
                    case R.id.nav_tech:
                        navItemIndex = 8;
                        CURRENT_TAG = TAG_FOUR;
                        break;
                    case R.id.nav_travel:
                        navItemIndex = 9;
                        CURRENT_TAG = TAG_FOUR;
                        break;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadNewsOneFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("Firebase reg id: ", "Firebase reg id: " + regId);
    }
}

