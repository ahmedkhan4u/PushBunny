package com.pushbunny;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

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

    NotificationModel model = NotificationAdapter.selectedItem.get(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);

        txtTitle = findViewById(R.id.title);
        txtMessage = findViewById(R.id.message);
        txtSubTitle = findViewById(R.id.subTitle);
        imageView = findViewById(R.id.imageUser);
        txtTime = findViewById(R.id.time);
        cardView = findViewById(R.id.cardView);
        deleteMessage = findViewById(R.id.deleteMessage);

        if (model.getOtherData().isEmpty() || model.getOtherData().equals("{}")){
            txtSubTitle.setVisibility(View.GONE);
        }

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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}