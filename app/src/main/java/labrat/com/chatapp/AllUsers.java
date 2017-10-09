package labrat.com.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsers extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);

        toolbar = (Toolbar) findViewById(R.id.AllUsers_navbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contacts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.users_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.user_profile,
                UsersViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users users, final int position) {
                viewHolder.setDisplayName(users.getName());
                viewHolder.setStatusValue(users.getStatus());
                viewHolder.setImageValue(users.getThumb_image(),getApplicationContext());

                final String user_id = getRef(position).getKey();
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profile = new Intent(AllUsers.this,profileActivity.class);
                        profile.putExtra("user_id",user_id);
                        startActivity(profile);
                    }
                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View view;
        public UsersViewHolder(View itemView) {
            super(itemView);

            view = itemView;
        }

        public void setDisplayName(String name){
            TextView textView = (TextView) view.findViewById(R.id.displayNameAllUsers);
            textView.setText(name);
        }

        public void setStatusValue(String name){
            TextView textView = (TextView) view.findViewById(R.id.StatusAllUsers);
            textView.setText(name);
        }

        public void setImageValue(final String imagevalue, final Context context){
            final CircleImageView imageView = (CircleImageView) view.findViewById(R.id.imageAllUsers);
            Picasso.with(context).load(imagevalue).placeholder(R.drawable.ca).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ca).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(imagevalue).placeholder(R.drawable.ca).into(imageView);
                }
            });

        }


    }
}
