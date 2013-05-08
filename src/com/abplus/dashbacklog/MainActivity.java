package com.abplus.dashbacklog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

public class MainActivity extends Activity {

    private BacklogIO backlog = null;
    private BackLogCache cache = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.tab_summaries).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSummaries();
            }
        });
        findViewById(R.id.tab_time_line).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeLine();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String space_id = prefs.getString(getString(R.string.key_space_id), "");
        String user_id = prefs.getString(getString(R.string.key_user_id), "");
        String password = prefs.getString(getString(R.string.key_password), "");
        int which = prefs.getInt(getString(R.string.key_displayed), -1);

        if (space_id.length() == 0 || user_id.length() == 0 || password.length() == 0) {
            Intent intent=new Intent(this, PrefsActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);
        } else {
            backlog = new BacklogIO(space_id, user_id, password);
            if (which == ListOf.SUMMARIES.ordinal()) {
                showSummaries();
            } else {
                showTimeLine();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //  どれを表示していたかを保存しておく
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(getString(R.string.key_displayed), whichDisplayed().ordinal());
    }

    private void showSummaries() {
        findViewById(R.id.tab_summaries).setBackgroundColor(getResources().getColor(R.color.bg_selected));
        findViewById(R.id.tab_time_line).setBackgroundColor(getResources().getColor(R.color.bg_unselected));
    }

    private void showTimeLine() {
        findViewById(R.id.tab_summaries).setBackgroundColor(getResources().getColor(R.color.bg_unselected));
        findViewById(R.id.tab_time_line).setBackgroundColor(getResources().getColor(R.color.bg_selected));
    }

    private void showComments(int commentId) {

    }

    private enum ListOf {
        SUMMARIES,
        TIME_LINE,
        COMMENTS
    }

    private ListOf whichDisplayed() {
        if (findViewById(R.id.tabs).getVisibility() == View.GONE) {
            return ListOf.COMMENTS;
        } else if (findViewById(R.id.tab_summaries).getTag() != null) {
            return ListOf.SUMMARIES;
        } else {
            return ListOf.TIME_LINE;
        }
    }
}
