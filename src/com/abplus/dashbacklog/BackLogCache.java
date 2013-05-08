package com.abplus.dashbacklog;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Copyright (C) 2013 ABplus Inc. kazhida
 * All rights reserved.
 * Author:  kazhida
 * Created: 2013/05/08 11:47
 */
public class BackLogCache {

    interface OnIssueClickListener {
        void onClick(View v, String key);
    }

    private BacklogIO backlogIO;
    private LayoutInflater inflater;
    private Context context;
    private User currentUser;

    private final String DEBUG_TAG = "dash_backlog.selection_cache";

    private BackLogCache(Activity activity, BacklogIO io) {
        inflater = activity.getLayoutInflater();
        context = activity;
        backlogIO = io;
    }

    static BackLogCache cache = null;

    static public BackLogCache initSharedInstance(Activity activity, BacklogIO io) {
        cache = new BackLogCache(activity, io);
        return cache;
    }

    static public BackLogCache sharedInstance() {
        return cache;
    }

    public String spaceId() {
        return backlogIO.getSpaceId();
    }

    public String userId() {
        return backlogIO.getUserId();
    }

    public int userIdAsInt() {
        return currentUser.id;
    }

    public void loadSummaries(final BacklogIO.ResponseNotify notify) {
        backlogIO.loadSummaries(new BacklogIO.ResponseNotify() {
            @Override
            public void success(int code, String response) {

            }

            @Override
            public void failed(int code, String response) {
                notify.failed(code, response);
            }

            @Override
            public void error(Exception e) {
                notify.error(e);
            }
        });
    }

    public class User {
        int     id;
        String  name;
        String  lang;
        String  updated_on;
    }

    public void loadUser(final BacklogIO.ResponseNotify notify) {

        backlogIO.loadUser(backlogIO.getUserId(), new BacklogIO.ResponseNotify() {

            @Override
            public void success(int code, String response) {
                UserParser parser = new UserParser();
                try {
                    parser.parse(response);
                    currentUser = parser.user;
                    notify.success(code, response);
                } catch (XmlPullParserException e) {
                    notify.error(e);
                } catch (IOException e) {
                    notify.error(e);
                }
            }

            @Override
            public void failed(int code, String response) {
                notify.failed(code, response);
            }

            @Override
            public void error(Exception e) {
                notify.error(e);
            }
        });
    }

    public BaseAdapter getSummariesAdapter() {
        //todo:後でちゃんとやる
        return null;
    }

    public BaseAdapter getTimeLineAdapter() {
        //todo:後でちゃんとやる
        return null;
    }

    public BaseAdapter getCommentsAdapter() {
        //todo:後でちゃんとやる
        return null;
    }


    private abstract class StructParser {

        void parse(String source) throws XmlPullParserException, IOException {
            XmlPullParser xpp = Xml.newPullParser();
            xpp.setInput(new StringReader(source));

            for (int et = xpp.getEventType(); et != XmlPullParser.END_DOCUMENT; et = xpp.next()) {
                if (et == XmlPullParser.START_DOCUMENT) {
                    Log.d(DEBUG_TAG, "Document start.");
                } else if (et == XmlPullParser.START_TAG) {
                    Log.d(DEBUG_TAG, "Start tag " + xpp.getName());
                    if (xpp.getName().equals("struct")) {
                        parseStruct(xpp);
                    }
                } else if (et == XmlPullParser.END_TAG) {
                    Log.d(DEBUG_TAG, "End tag " + xpp.getName());
                } else if (et == XmlPullParser.TEXT) {
                    Log.d(DEBUG_TAG, "Text " + xpp.getText());
                }
            }
            Log.d(DEBUG_TAG, "Document end.");
        }

        void parseStruct(XmlPullParser xpp) throws IOException, XmlPullParserException {

            for (int et = xpp.next(); et != XmlPullParser.END_DOCUMENT; et = xpp.next()) {
                if (et == XmlPullParser.START_TAG) {
                    Log.d(DEBUG_TAG + ".parseStruct", "Start tag " + xpp.getName());
                    if (xpp.getName().equals("member")) {
                        parseMember(xpp);
                    }
                } else if (et == XmlPullParser.END_TAG) {
                    Log.d(DEBUG_TAG + ".parseStruct", "End tag " + xpp.getName());
                    if (xpp.getName().equals("struct")) break;
                } else if (et == XmlPullParser.TEXT) {
                    Log.d(DEBUG_TAG + ".parseStruct", "Text " + xpp.getText());
                }
            }
        }

        void parseMember(XmlPullParser xpp) throws IOException, XmlPullParserException {
            String tag = "";
            String name = null;

            for (int et = xpp.next(); et != XmlPullParser.END_DOCUMENT; et = xpp.next()) {
                if (et == XmlPullParser.START_TAG) {
                    Log.d(DEBUG_TAG + ".parseMember", "Start tag " + xpp.getName());
                    tag = xpp.getName();
                    if (tag.equals("value")) {
                        parseValue(name, xpp);
                    }
                } else if (et == XmlPullParser.END_TAG) {
                    Log.d(DEBUG_TAG + ".parseMember", "End tag " + xpp.getName());
                    if (xpp.getName().equals("member")) break;
                } else if (et == XmlPullParser.TEXT) {
                    Log.d(DEBUG_TAG + ".parseMember", "Text " + xpp.getText());
                    if (tag.equals("name")) {
                        name = xpp.getText();
                    }
                }
            }
        }

        abstract void parseValue(String name, XmlPullParser xpp) throws IOException, XmlPullParserException;
    }

    private class UserParser extends StructParser {
        User user = new User();

        @Override
        void parseValue(String name, XmlPullParser xpp) throws IOException, XmlPullParserException {
            for (int et = xpp.next(); et != XmlPullParser.END_DOCUMENT; et = xpp.next()) {
                if (et == XmlPullParser.START_TAG) {
                    Log.d(DEBUG_TAG + ".parseValue", "Start tag " + xpp.getName());
                } else if (et == XmlPullParser.END_TAG) {
                    Log.d(DEBUG_TAG + ".parseValue", "End tag " + xpp.getName());
                    if (xpp.getName().equals("value")) break;
                } else if (et == XmlPullParser.TEXT) {
                    Log.d(DEBUG_TAG + ".parseValue", "Text " + xpp.getText());
                    if (user != null) {
                        if (name.equals("id")) {
                            user.id = Integer.parseInt(xpp.getText());
                        } else if (name.equals("name")) {
                            user.name = xpp.getText();
                        } else if (name.equals("lang")) {
                            user.lang = xpp.getText();
                        } else if (name.equals("updated_on")) {
                            user.updated_on = xpp.getText();
                        }
                    }
                }
            }
        }
    }
}
