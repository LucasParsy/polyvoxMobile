package com.tuxlu.polyvox.Room;

import android.util.Log;

import org.jibble.pircbot.PircBot;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tuxlu on 16/01/18.
 */


public class IRCChat {
    private IRCReceiver receiver = null;
    private List<RoomChatResult> messages = new ArrayList<RoomChatResult>();
    private boolean banned = false;
    private String chan = "";
    //private List<String> bannedChannels; //todo: if multichannel support


    public class IRCReceiver extends PircBot {

        public IRCReceiver(String name) {
/*
            this.setName(name);
            this.setLogin(name);
            this.setVerbose(false);
            try {
                this.connect("irc.freenode.net");
            } catch (Exception e) {
                //Log.wtf("IRCCHAT", e.getCause());
                e.printStackTrace();
            }
            this.joinChannel("##linux");
            this.joinChannel("#python");
            this.joinChannel("#debian");
*/

            this.setName("polyvox_test");
            this.setLogin("polyvox_test");
            this.setVerbose(false);
            try {
                this.connect("irc.chat.twitch.tv", 6667, "oauth:pln7gksdq9ty1x8a645uv7zfbknqqp");
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.joinChannel("#lemilien");
            this.joinChannel("#mistermv");
            this.joinChannel("#twitchplayspokemon");


        }

        @Override
        public void onMessage(String channel, String sender,
                              String login, String hostname, String message) {

            if (!chan.isEmpty() && !channel.equals(chan))
                return;
            if (channel.isEmpty())
                chan = channel;

            RoomChatResult res = new RoomChatResult(sender, message);
            messages.add(res);
        }

        @Override
        public void onJoin(String channel, String sender, String login, String hostname) {
        }

        @Override
        protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
            if (recipientNick.equals(this.getNick()))
                banned = true;
        }

    }


    IRCChat(String username, String channel) {

        //if (username == null || username.isEmpty()) //todo: modify all the twitch tweaks
            username = "polyvox_test";
        String finalUsername = username;
        new Thread() {
            public void run() {
                receiver = new IRCReceiver(finalUsername);
            }
        }.start();
    }


    public boolean isBanned() {
        boolean res = banned;
        banned = false;
        return res;
    }

    List<RoomChatResult> getMessages() {
        List<RoomChatResult> res = new ArrayList<RoomChatResult>(messages);
        messages.clear();
        return res;
    }

    void sendMessage(String message) {
        if (!chan.isEmpty()) {
            new Thread() {
                public void run() {
                    receiver.sendMessage(chan, message);
                }
            }.start();
        }
    }
}
