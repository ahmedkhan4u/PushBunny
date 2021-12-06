package com.pushbunny;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ComponentActivity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.pushbunny.Adapters.NotificationAdapter;
import com.pushbunny.Models.NotificationModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewMessageActivity extends AppCompatActivity {

    //319046
    private TextView txtMessage, txtTitle, txtSubTitle, txtTime;
    private ImageView imageView;
    private CardView cardView;
    private ImageView deleteMessage;
    private AppCompatButton btnUrl;

    NotificationModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);

        if (MainActivity.tempList.size() > 0) {
            model = MainActivity.tempList.get(0);
        }else{
            model = NotificationAdapter.selectedItem.get(0);
        }

        txtTitle = findViewById(R.id.title);
        txtMessage = findViewById(R.id.message);
        txtSubTitle = findViewById(R.id.subTitle);
        imageView = findViewById(R.id.imageUser);
        txtTime = findViewById(R.id.time);
        cardView = findViewById(R.id.cardView);
        deleteMessage = findViewById(R.id.deleteMessage);
        btnUrl = findViewById(R.id.btnUrl);

        if (model.getOtherData().isEmpty() || model.getOtherData().equals("{}")){
            txtSubTitle.setVisibility(View.GONE);
        }


        Log.d("dxdiag" , model.getOtherData().toString());

        deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ViewMessageActivity.this, deleteMessage );
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.delete_message, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewMessageActivity.this);
                        builder1.setTitle("Delete Message");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        MainActivity.adapter.removeItem(NotificationAdapter.pos, model);
                                        onBackPressed();

//                                        Integer deletedRows = MainActivity.db.deleteData(model.getId());
//                                        if (deletedRows > 0){
//
//
//                                            MainActivity.adapter.notifyItemRemoved(NotificationAdapter.pos);
//                                            MainActivity.adapter.notifyDataSetChanged();
//                                            onBackPressed();
//
////                                            Intent intent = new Intent(ViewMessageActivity.this, MainActivity.class);
////                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                            startActivity(intent);
////                                            finish();
//                                        }
                                    }
                                });

                        builder1.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                        alert11.show();



                        return true;
                    }
                });

                popup.show(); //showing popup menu


            }
        });

        findViewById(R.id.onBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                MainActivity.tempList.clear();
            }
        });

        txtMessage.setText(model.getMessage());
        if(!model.getImage().isEmpty()){

            Picasso.get().load(model.getImage()).placeholder(R.drawable.ic_image_holder).fit()
                    .centerInside()
                    .into(imageView);

        } else {
            cardView.setVisibility(View.GONE);
        }
        txtTitle.setText(model.getTitle());
        txtTime.setText(GetTimeAgo.getTimeAgo(Long.parseLong(model.getTime()), this));
        try {
            JSONObject object = new JSONObject(model.getOtherData());
            txtSubTitle.setText(object.getString("subtitle"));

            String url = object.getString("url");
            String urlTitle = object.getString("url-title");

            Log.d("dxdiag", "Url => " + url);

            if (!url.equals("")){
                btnUrl.setVisibility(View.VISIBLE);
                btnUrl.setText(urlTitle);
                btnUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                });
            }else {
                btnUrl.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.tempList.clear();
        super.onBackPressed();
    }
}