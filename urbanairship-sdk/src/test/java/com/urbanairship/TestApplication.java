package com.urbanairship;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentProvider;

import com.urbanairship.analytics.Analytics;
import com.urbanairship.location.UALocationManager;
import com.urbanairship.push.PushManager;
import com.urbanairship.richpush.RichPushInbox;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.TestLifecycleApplication;
import org.robolectric.shadows.ShadowContentResolver;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

public class TestApplication extends Application implements TestLifecycleApplication {

    public ActivityLifecycleCallbacks callback;
    public PreferenceDataStore preferenceDataStore;

    @Override
    public void onCreate() {
        super.onCreate();

        this.preferenceDataStore = new PreferenceDataStore(getApplicationContext());
        preferenceDataStore.executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };

        AirshipConfigOptions airshipConfigOptions = new AirshipConfigOptions.Builder()
                .setDevelopmentAppKey("app_key")
                .setDevelopmentAppSecret("app_secret")
                .setInProduction(false)
                .build();

        UAirship.application = this;
        UAirship.isFlying = true;
        UAirship.isTakingOff = true;

        UrbanAirshipProvider.init();
        ContentProvider provider = new UrbanAirshipProvider();
        provider.onCreate();

        UAirship.sharedAirship = new UAirship(this, airshipConfigOptions, preferenceDataStore);

        setPlatform(UAirship.ANDROID_PLATFORM);
        ShadowContentResolver.registerProvider(UrbanAirshipProvider.getAuthorityString(), provider);
    }

    public void setPlatform(int platform) {
        preferenceDataStore.put("com.urbanairship.application.device.PLATFORM", platform);
    }

    public static TestApplication getApplication() {
        return (TestApplication) RuntimeEnvironment.application;
    }

    public void setApplicationMetrics(ApplicationMetrics metrics) {
        UAirship.shared().applicationMetrics = metrics;
    }

    public void setAnalytics(Analytics analytics) {
        UAirship.shared().analytics = analytics;
    }

    public void setOptions(AirshipConfigOptions options) {
        UAirship.shared().airshipConfigOptions = options;
    }

    @Override
    public void afterTest(Method method) {
    }

    @Override
    public void beforeTest(Method method) {
    }

    @Override
    public void prepareTest(Object test) {

    }

    @Override
    @SuppressLint("NewApi")
    public void registerActivityLifecycleCallbacks(
            Application.ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);
        this.callback = callback;
    }

    public void setPushManager(PushManager pushManager) {
        UAirship.shared().pushManager = pushManager;
    }

    public void setLocationManager(UALocationManager locationManager) {
        UAirship.shared().locationManager = locationManager;
    }

    public void setInbox(RichPushInbox inbox) {
        UAirship.shared().inbox = inbox;
    }
}
