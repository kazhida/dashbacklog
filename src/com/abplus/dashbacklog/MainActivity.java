package com.abplus.dashbacklog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;

public class MainActivity extends Activity {

    private enum ListOf {
        SUMMARIES,
        TIME_LINE,
        COMMENTS
    }

    private BacklogIO backlog = null;
    private BackLogCache cache = null;
    private ListOf which = ListOf.SUMMARIES;
    private MenuItem postItem = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setTheme(R.style.app_theme);
        }
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
        int displayed = prefs.getInt(getString(R.string.key_displayed), -1);

        if (space_id.length() == 0 || user_id.length() == 0 || password.length() == 0) {
            showPreferences();
        } else {
            backlog = new BacklogIO(space_id, user_id, password);
            if (displayed != ListOf.TIME_LINE.ordinal()) {
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
        editor.putInt(getString(R.string.key_displayed), which.ordinal());
        editor.commit();
    }

    private void showPreferences() {
        Intent intent=new Intent(this, PrefsActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    private void showSummaries() {
        which = ListOf.SUMMARIES;

        findViewById(R.id.tab_summaries).setBackgroundColor(getResources().getColor(R.color.bg_selected));
        findViewById(R.id.tab_time_line).setBackgroundColor(getResources().getColor(R.color.bg_unselected));
        postItem.setVisible(false);
    }

    private void showTimeLine() {
        which = ListOf.TIME_LINE;

        findViewById(R.id.tab_summaries).setBackgroundColor(getResources().getColor(R.color.bg_unselected));
        findViewById(R.id.tab_time_line).setBackgroundColor(getResources().getColor(R.color.bg_selected));
        postItem.setVisible(false);
    }

    private void showComments(int commentId) {
        which = ListOf.COMMENTS;

        postItem.setVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);

        postItem = menu.findItem(R.id.menu_post);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_config:
                showPreferences();
                return true;
            case R.id.menu_reload:
                return true;
            case R.id.menu_post:
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && which == ListOf.COMMENTS) {
            showTimeLine();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
