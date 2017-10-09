package labrat.com.chatapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private RecyclerView frList;

    private DatabaseReference friendDb;
    private DatabaseReference usersDb;
    private FirebaseAuth firebaseAuth;
    String uid;

    private View mView;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_contacts,container,false);

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        friendDb = FirebaseDatabase.getInstance().getReference().child("Friends").child(uid);
        friendDb.keepSynced(true);
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        usersDb.keepSynced(true);
        frList = (RecyclerView) mView.findViewById(R.id.friends_list_fragment);
        //frList.setHasFixedSize(true);
        frList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> friendsRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.user_profile,
                FriendsViewHolder.class,
                friendDb
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {
                viewHolder.setDate(model.getDate());

                final String uidOfuser = getRef(position).getKey();
                usersDb.child(uidOfuser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String thumb = dataSnapshot.child("thumb_image").getValue().toString();

                        viewHolder.setName(name);
                        viewHolder.setThumb(thumb,getContext());

                        if (dataSnapshot.hasChild("online")){
                            Boolean onlineStatus = (Boolean) dataSnapshot.child("online").getValue();
                            viewHolder.setUserOnline(onlineStatus);
                        }

                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{"Open Profile","Send Message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Option");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0){
                                            Intent profile = new Intent(getContext(),profileActivity.class);
                                            profile.putExtra("user_id",uidOfuser);
                                            startActivity(profile);
                                        }
                                        if (which)
                                    }
                                });

                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };


        frList.setAdapter(friendsRecyclerAdapter);
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View view;
        public FriendsViewHolder(View itemView) {
            super(itemView);

            view = itemView;
        }

        public void setDate(String date){
            TextView textView = (TextView) view.findViewById(R.id.StatusAllUsers);
            textView.setText("Friends Since : "+date);
        }

        public void setName(String name){
            TextView textView = (TextView) view.findViewById(R.id.displayNameAllUsers);
            textView.setText(name);
        }

        public void setThumb(final String imagevalue, final Context context) {
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

        public void setUserOnline(Boolean olineState){
            ImageView imageView = (ImageView) view.findViewById(R.id.userOnlineIconProfile);
            if (olineState){
                imageView.setVisibility(View.VISIBLE);
            }
            else {
                imageView.setVisibility(View.INVISIBLE);
            }
        }

    }
}
