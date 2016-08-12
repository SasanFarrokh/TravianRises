package ir.cdesign.travianrises;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.util.Timer;
import java.util.TimerTask;


public class LoginActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    String email;
    TextView titleText,subtitleText;
    View titleCon;
    GoogleApiClient googleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }
    @Override
    protected void onStart() {
        super.onStart();

        titleText = (TextView) findViewById(R.id.title_text);
        subtitleText = (TextView) findViewById(R.id.subtitle_text);
        titleCon = findViewById(R.id.title_container);

        titleCon.setScaleX(0.8f);
        titleCon.setScaleY(0.8f);
        titleText.setAlpha(0.f);
        subtitleText.setAlpha(0.f);
        titleText.animate().alpha(1.f).setStartDelay(800).setDuration(3000).start();
        subtitleText.animate().alpha(1.f).setStartDelay(3800).setDuration(1000).start();
        titleCon.animate().scaleX(1.1f).scaleY(1.1f).setDuration(12000).start();

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            String[] accTypes = new String[]{"com.google"};
            Intent intent = AccountPicker.newChooseAccountIntent(null,null,accTypes,false,null,null,null,null);
            @Override
            public void run() {
                startActivityForResult(intent,LoginActivity.REQUEST_CODE_PICK_ACCOUNT);
            }
        };
        new BackgroundTask(LoginActivity.this).execute("main.php","1");
        timer.schedule(timerTask,6000);
        //googleApiClient.connect();
    }


    public void startGame() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
    public static void networkPopup(final Activity a) {
        new AlertDialog.Builder(a)
                .setTitle("No Internet Connection")
                .setMessage("Please make sure you have connected to a network...")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(a,LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        a.finish();
                        a.overridePendingTransition(0, 0);

                        a.startActivity(intent);
                        a.overridePendingTransition(0, 0);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        a.finish();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        a.finish();
                    }
                })
                .show();
    }
    public static boolean checkNetwork(Activity a) {

        ConnectivityManager conMgr = (ConnectivityManager) a.getSystemService (CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;
        } else return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {

            if ( resultCode == RESULT_OK) {
                email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                Toast.makeText(this,email,Toast.LENGTH_SHORT);
                startGame();
            } else {
                String[] accTypes = new String[]{"com.google"};
                Intent intent = AccountPicker.newChooseAccountIntent(null,null,accTypes,false,null,null,null,null);
                startActivityForResult(intent,LoginActivity.REQUEST_CODE_PICK_ACCOUNT);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startGame();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(LoginActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
        startGame();
    }
}
