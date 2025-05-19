package com.example.qlct.Notification;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlct.ItemTouchHelperListener;
import com.example.qlct.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Notification_activity extends AppCompatActivity {
    ArrayList<String> listxoa = new ArrayList<>();
    private RecyclerView recyclerView;
    private Notification_Adapter notificationAdapter;
    private ArrayList<Notification_class> notificationList;
    RelativeLayout relativeLayout;

    // Mock data class to replace GetAllNotificationEntity
    private static class MockNotification {
        private static class NotificationDetails {
            String title;
            String body;
            String imageUrl;

            NotificationDetails(String title, String body, String imageUrl) {
                this.title = title;
                this.body = body;
                this.imageUrl = imageUrl;
            }

            String getTitle() {
                return title;
            }

            String getBody() {
                return body;
            }

            String getImageUrl() {
                return imageUrl;
            }
        }

        String id;
        NotificationDetails notification;
        String date;

        MockNotification(String id, String title, String body, String imageUrl, String date) {
            this.id = id;
            this.notification = new NotificationDetails(title, body, imageUrl);
            this.date = date;
        }

        String getId() {
            return id;
        }

        NotificationDetails getNotification() {
            return notification;
        }

        String getDate() {
            return date;
        }
    }

    // Mock data to replace API calls
    private List<MockNotification> getMockNotifications() {
        return new ArrayList<>(Arrays.asList(
                new MockNotification("1", "Budget Alert", "You have exceeded your monthly budget!", "img_budget.png", "2025-05-18"),
                new MockNotification("2", "Transaction Added", "New transaction of $50 added to Food category.", "img_transaction.png", "2025-05-17"),
                new MockNotification("3", "Savings Goal", "You're 80% towards your savings goal!", "img_savings.png", "2025-05-16"),
                new MockNotification("4", "Bill Reminder", "Electricity bill due tomorrow.", "img_bill.png", "2025-05-15")
        ));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaiton);

        TextView cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> finish());

        relativeLayout = findViewById(R.id.main);
        recyclerView = findViewById(R.id.rcv_notic);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        getListnoti();
        notificationAdapter = new Notification_Adapter(notificationList);
        recyclerView.setAdapter(notificationAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecycleView_itemTouchHelper(0, ItemTouchHelper.LEFT, viewHolder -> {
            if (viewHolder instanceof Notification_Adapter.NotiHolder) {
                String nameDeleted = notificationList.get(viewHolder.getAdapterPosition()).getHeader();
                String id = notificationList.get(viewHolder.getAdapterPosition()).getId();

                listxoa.add(id);
                Notification_class deleted = notificationList.get(viewHolder.getAdapterPosition());
                int indexdelte = viewHolder.getAdapterPosition();
                notificationAdapter.RemoveItem(indexdelte);

                Snackbar snackbar = Snackbar.make(relativeLayout, nameDeleted + " removed", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", v -> {
                    notificationAdapter.UndoItem(indexdelte, deleted);
                    listxoa.remove(id);
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        });

        ConstraintLayout clear = findViewById(R.id.clearall);
        clear.setOnClickListener(v -> new AlertDialog.Builder(Notification_activity.this)
                .setTitle("Warning")
                .setMessage("Are you sure you want to clear all notifications?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    for (int i = 0; i < notificationList.size(); i++) {
                        listxoa.add(notificationList.get(i).getId());
                    }
                    for (int i = notificationList.size() - 1; i >= 0; i--) {
                        notificationAdapter.RemoveItem(i);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    private void getListnoti() {
        notificationList = new ArrayList<>();
        List<MockNotification> mockNotifications = getMockNotifications();

        if (!mockNotifications.isEmpty()) {
            for (MockNotification item : mockNotifications) {
                try {
                    notificationList.add(new Notification_class(
                            item.getId(),
                            item.getNotification().getTitle(),
                            item.getNotification().getBody(),
                            item.getNotification().getImageUrl(),
                            1,
                            item.getDate()
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Simulate deletion of notifications in listxoa
        listxoa.clear();
    }
}