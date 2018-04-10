package com.my;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.SubscriptionPurchase;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

public class Main {

    private static Optional<GoogleCredential> getCredentials(String filename) {
        try {
            GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(filename))
                    .createScoped(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER));
            return Optional.of(credential);
        }
        catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static Optional<AndroidPublisher> getPublisher(GoogleCredential credential, String applicationName) {
        try {
            AndroidPublisher publisher = new AndroidPublisher.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential)
                    .setApplicationName(applicationName)
                    .build();
            return Optional.of(publisher);
        }
        catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Please provide packageName, productId, and token");
            System.out.println("Example: googledeveloperapi ru.mail.mrgservicetest ru.mail.mrgs.subs1 mdcbmbnehpbchinnidnfcmii.AO-J1OynwdyvcS5HGiLRi1x3xY9p2uFjfP74G13pX6J7TTx6DBrMdfa3yEGCSFvAku-TAzebkSBEOa1vX7pohemwY-y6kEOUZBk1Ieqcogc51Y_M9t9grOlxGWgvQthogp9bNKTE-sEJ");
            return;
        }

        String applicationName = args[0];
        String productId = args[1];
        String token = args[2];

        System.out.println("packagename: "+ applicationName);
        System.out.println("productId: "+ productId);
        System.out.println("token: "+ token);
        System.out.println();

        Optional<GoogleCredential> credential = getCredentials("resources/google-api.json");
        if (!credential.isPresent()) {
            System.out.println("Can not get credentials from file");
            return;
        }
        Optional<AndroidPublisher> publisher = getPublisher(credential.get(), applicationName);
        if (!publisher.isPresent()) {
            System.out.println("Can not create Publisher client");
            return;
        }
        try {
            AndroidPublisher.Purchases.Subscriptions.Get get = publisher.get().purchases().subscriptions().get(applicationName, productId, token);
            SubscriptionPurchase purchase = get.execute();
            System.out.print(purchase.toPrettyString());
            System.out.println();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldn't get product info");
        }
    }
}
