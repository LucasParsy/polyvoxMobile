package com.tuxlu.polyvox.Utils.Auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.tuxlu.polyvox.Utils.Auth.Authenticator;

/**
 * Created by tuxlu on 30/09/17.
 */

public class AuthenticatorService extends Service {
        @Override
        public IBinder onBind(Intent intent) {
            Authenticator authenticator = new Authenticator(this);
            return authenticator.getIBinder();
        }
    }