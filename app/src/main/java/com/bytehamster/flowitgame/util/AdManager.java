package com.bytehamster.flowitgame.util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.net.MalformedURLException;
import java.net.URL;

public class AdManager {
    private final Context context;
    private final AdView adView;
    private ConsentForm consentForm;
    private final ConsentInformation consentInformation;
    private boolean popupAllowed = true;


    public AdManager(Context context, AdView adView) {
        this.context = context;
        this.adView = adView;

        consentInformation = ConsentInformation.getInstance(context);
        MobileAds.initialize(context.getApplicationContext(), "ca-app-pub-8233037560237995~7887041460");
    }

    public void loadAd() {
        if (!isFirstLevelSolved()) {
            Log.d("AdManager", "First run - displaying no ads");
            return;
        }

        Log.d("AdManager", "Requesting consent info");
        String[] publisherIds = {"pub-8233037560237995"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d("AdManager", "onConsentInfoUpdated: " + consentStatus.name());

                if (!consentInformation.isRequestLocationInEeaOrUnknown()) {
                    Log.d("AdManager", "Location not in EU");
                    loadAd(true);
                    return;
                }

                switch (consentStatus) {
                    case PERSONALIZED:
                        loadAd(true);
                        break;
                    case NON_PERSONALIZED:
                        loadAd(false);
                        break;
                    default:
                        askConsent();
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                Log.e("AdManager", errorDescription);
            }
        });
    }

    public void askConsent() {
        Log.d("AdManager", "Asking for consent");
        URL privacyUrl;
        try {
            privacyUrl = new URL("http://www.tools.bytehamster.com/privacy/flowit.txt");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        consentForm = new ConsentForm.Builder(context, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        consentInformation.setConsentStatus(consentStatus);

                        switch (consentStatus) {
                            case PERSONALIZED:
                                loadAd(true);
                                break;
                            case NON_PERSONALIZED:
                                loadAd(false);
                                break;
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.e("AdManager", errorDescription);
                    }

                    @Override
                    public void onConsentFormLoaded() {
                        if (popupAllowed) {
                            consentForm.show();
                        }
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        consentForm.load();
    }

    private void loadAd(boolean personalized) {
        Log.d("AdManager", "Loading ads. Personalized: " + personalized);
        AdRequest.Builder builder = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("60A0C2C6A9877637B6A090ECCFD5CDD9");
        if (!personalized) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }
        AdRequest adRequest = builder.build();
        adView.loadAd(adRequest);
    }

    private boolean isFirstLevelSolved() {
        return context.getSharedPreferences("playedState", Context.MODE_PRIVATE).getBoolean("l1", false);
    }

    public void setPopupAllowed(boolean popupAllowed) {
        this.popupAllowed = popupAllowed;
    }
}
