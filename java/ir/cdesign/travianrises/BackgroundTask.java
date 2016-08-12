package ir.cdesign.travianrises;

import android.app.Activity;
import android.os.AsyncTask;
import android.app.Fragment;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BackgroundTask extends AsyncTask<String,Void,String> {

    public static final int MAIN = 1;
    public static final int ARMYCAMP = 3;
    public static final int MAP = 2;
    public static final int MESSAGES = 4;
    public static final int MESSAGE_TEXT = 5;

    private static final String DOMAIN_NAME = "http://caspian-food.com/travian/";

    static boolean updated = false;
    static JSONObject jsonMain = new JSONObject();
    static JSONObject jsonMessages = new JSONObject();
    public static Toast conMsg = null;
    public static MainActivity mainActivity;
    public static Fragment fragment;
    public BackgroundTask(Activity a) {

        if ( !LoginActivity.checkNetwork(a) ) {
            LoginActivity.networkPopup(a);
        }

        if ( a instanceof MainActivity ) {
            mainActivity = (MainActivity) a;



        } else {
            mainActivity = null;
        }

    }
    public BackgroundTask(Fragment a) {
        fragment = a;
    }

    private int type;

    @Override
    protected String doInBackground(String... params) {

        type = Integer.parseInt(params[1]);
        try {
            URL url = new URL(DOMAIN_NAME + params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String response = null;
            while ( (response = bufferedReader.readLine()) != null ) {
                stringBuilder.append(response);
            }
            return stringBuilder.toString().trim();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } return "";

    }

    @Override
    protected void onPostExecute(String s) {
        try {
            if (!updated) updated = true;
            switch (type) {

                case MAIN:
                    jsonMain = new JSONObject(s);
                    if ( mainActivity != null ) {
                        showMain();
                    }
                    return;
                case MESSAGES:
                    jsonMessages = new JSONObject(s);
                    if ( mainActivity != null ) {
                        showMessageContacts();
                    }
                    return;
                case MESSAGE_TEXT:
                    jsonMessages = new JSONObject(s);
                    if ( mainActivity != null ) {
                        showMessageFromId();
                    }
                    return;
                default: return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (!updated) updated = true;
        }

    }

    public static void showMain()  {

        JSONObject user = null;
        if ( updated ) {
            try {
                user = jsonMain.getJSONObject("user");
                String name = user.getString("name");
                String village = jsonMain.getString("villageName");
                TextView textView = (TextView) mainActivity.findViewById(R.id.jsonview);
                if ( textView != null ) {
                    textView.setText("username : " + name + ", Village Name : " + village);
                }

            } catch (JSONException e) {
                LoginActivity.networkPopup(mainActivity);
            }
        } else  {
            new BackgroundTask(mainActivity).execute("http://caspian-food.com/travian/main.php", "1");
            if ( conMsg != null ) {conMsg.cancel(); conMsg = null;}
            conMsg = Toast.makeText(mainActivity,"Loading...",Toast.LENGTH_SHORT);
            conMsg.show();
        }
    }
    public static void showRes() {
        JSONObject resources = null;
        if ( updated ) {
            try {
                resources = jsonMain.getJSONObject("resources");
                TextView wood_amt = (TextView) mainActivity.findViewById(R.id.wood_amt);
                wood_amt.setText(resources.getInt("wood") + "/" + resources.getInt("storage"));
                TextView clay_amt = (TextView) mainActivity.findViewById(R.id.clay_amt);
                clay_amt.setText(resources.getInt("clay") + "/" + resources.getInt("storage"));
                TextView iron_amt = (TextView) mainActivity.findViewById(R.id.iron_amt);
                iron_amt.setText(resources.getInt("iron") + "/" + resources.getInt("storage"));
                TextView crop_amt = (TextView) mainActivity.findViewById(R.id.crop_amt);
                crop_amt.setText(resources.getInt("crop") + "/" + resources.getInt("storage"));
                TextView crop_use = (TextView) mainActivity.findViewById(R.id.cropuse_amt);
                crop_use.setText(resources.getInt("cropUse") + "/" + jsonMain.getJSONObject("production").getInt("crop"));
                ProgressBar wood_prog = (ProgressBar) mainActivity.findViewById(R.id.wood_progress);
                wood_prog.setProgress((resources.getInt("wood") / resources.getInt("storage")) * 100);
                ProgressBar clay_prog = (ProgressBar) mainActivity.findViewById(R.id.clay_progress);
                clay_prog.setProgress((resources.getInt("wood") / resources.getInt("storage")) * 100);
                ProgressBar iron_prog = (ProgressBar) mainActivity.findViewById(R.id.iron_progress);
                iron_prog.setProgress((resources.getInt("wood") / resources.getInt("storage")) * 100);
                ProgressBar crop_prog = (ProgressBar) mainActivity.findViewById(R.id.crop_progress);
                crop_prog.setProgress((resources.getInt("wood") / resources.getInt("cropStorage")) * 100);

            } catch (JSONException e) {
                e.printStackTrace();
                if ( conMsg != null ) {conMsg.cancel(); conMsg = null;}
                conMsg = Toast.makeText(mainActivity,"Unable To Connect",Toast.LENGTH_SHORT);
                conMsg.show();
            }
        } else  {
            new BackgroundTask(mainActivity).execute("http://caspian-food.com/travian/main.php", "1");
            if ( conMsg != null ) {conMsg.cancel(); conMsg = null;}
            conMsg = Toast.makeText(mainActivity,"Loading...",Toast.LENGTH_SHORT);
            conMsg.show();
        }
    }

    public void showTroops() {
        int tribe;
        String tribename = null;
        JSONArray troops = null;
        if ( updated ) {
            try {
                tribe = jsonMain.getJSONObject("user").getInt("tribe");
                troops = jsonMain.getJSONArray("troops");
                ViewGroup troops_con = (ViewGroup) mainActivity.findViewById(R.id.troops_con);
                switch (tribe) {
                    case 1:
                        tribename = "roman";
                        break;
                    case 2:
                        tribename = "teuton";
                        break;
                    case 3:
                        tribename = "gaul";
                        break;
                    default: break;
                }
                tribe = (tribe-1)*10;
                for (int i = 0; i < 10 ; i++) {

                    ViewGroup view = (ViewGroup) troops_con.getChildAt(i);
                    ImageView image = (ImageView) view.getChildAt(0);
                    int id = mainActivity.getResources().getIdentifier("ic_"+tribename+"_u"+(i+1),"drawable",mainActivity.getPackageName());
                    image.setImageResource(id);
                    TextView text = (TextView) view.getChildAt(1);
                    text.setText(String.valueOf( troops.getInt(i + tribe)  ));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                if ( conMsg != null ) {conMsg.cancel(); conMsg = null;}
                conMsg = Toast.makeText(mainActivity,"Unable To Connect",Toast.LENGTH_SHORT);
                conMsg.show();
            }
        } else  {
            new BackgroundTask(mainActivity).execute("main.php", "1");
            if ( conMsg != null ) {conMsg.cancel(); conMsg = null;}
            conMsg = Toast.makeText(mainActivity,"Loading...",Toast.LENGTH_SHORT);
            conMsg.show();
        }
    }
    public static void showMessageContacts() {

        MessageAdapter adapter = null;
        try {
            adapter = new MessageAdapter(mainActivity,jsonMessages.getJSONArray("contacts"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MessageAdapter.adapter = adapter;
        ListView listView = (ListView) mainActivity.findViewById(R.id.chat_listview);
        if ( listView != null ){
            listView.setAdapter(adapter);
        }
    }
    public static void showMessageFromId() {
        MessageAdapter adapter = null;
        try {
            adapter = new MessageAdapter(mainActivity,jsonMessages.getJSONArray("messages"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ListView listView = (ListView) mainActivity.findViewById(R.id.chat_msgview);
        if ( listView != null ){
            listView.setAdapter(adapter);
        }
    }
    public static void showVillages() {
        GridAdapter adapter = null;
        try {
            adapter = new GridAdapter(mainActivity,jsonMain.getJSONArray("villages"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GridAdapter.adapter = adapter;
        GridView gridView = (GridView) mainActivity.findViewById(R.id.villages_gridview);
        if ( gridView != null ){
            gridView.setAdapter(adapter);
        }
    }
    public static void showHero() {
        TextView name = (TextView) mainActivity.findViewById(R.id.hero_name);
        ViewGroup con = (ViewGroup) mainActivity.findViewById(R.id.hero_stat_con);
        ProgressBar hp = (ProgressBar) mainActivity.findViewById(R.id.hero_hp);
        ProgressBar xp = (ProgressBar) mainActivity.findViewById(R.id.hero_xp);
        ProgressBar ap = (ProgressBar) ((ViewGroup) mainActivity.findViewById(R.id.hero_ap)).getChildAt(0);
        ProgressBar ab = (ProgressBar) ((ViewGroup) mainActivity.findViewById(R.id.hero_ab)).getChildAt(0);
        ProgressBar db = (ProgressBar) ((ViewGroup) mainActivity.findViewById(R.id.hero_db)).getChildAt(0);
        ProgressBar res = (ProgressBar) ((ViewGroup) mainActivity.findViewById(R.id.hero_res)).getChildAt(0);
        try {
            JSONObject hero = jsonMain.getJSONObject("hero");
            name.setText(jsonMain.getJSONObject("user").getString("name"));
            hp.setProgress(((Double) hero.getDouble("health")).intValue());
            xp.setProgress(hero.getInt("experience") / 1000000);
            ap.setProgress( hero.getJSONObject("spentPoints").getInt("power"));
            ab.setProgress( hero.getJSONObject("spentPoints").getInt("att"));
            db.setProgress( hero.getJSONObject("spentPoints").getInt("def"));
            res.setProgress( hero.getJSONObject("spentPoints").getInt("product"));

            String freepoint = (hero.getInt("freePoints") > 0)?String.valueOf(hero.getInt("freePoints")):"";
            TextView free = (TextView) mainActivity.findViewById(R.id.hero_free_point1);
            free.setText(freepoint);
            free = (TextView) mainActivity.findViewById(R.id.hero_free_point2);
            free.setText(freepoint);
            free = (TextView) mainActivity.findViewById(R.id.hero_free_point3);
            free.setText(freepoint);
            free = (TextView) mainActivity.findViewById(R.id.hero_free_point4);
            free.setText(freepoint);
        } catch (NullPointerException e) {

        } catch (JSONException e) {

        }
    }
}
