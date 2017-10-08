package labrat.com.chatapp;

import android.icu.text.DateFormat;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class profileActivity extends AppCompatActivity {

    private TextView insDisplayNameProfile;
    private ImageView insDisplayImageProfile;
    private TextView insStatusProfile,insFriendCount;
    private Button sendRequestProfile;
    private DatabaseReference insDatabaseRef;
    private DatabaseReference friendRequestRef;
    private FirebaseUser firebaseUser;
    private String currentState ;
    private DatabaseReference friendsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");
        insDisplayNameProfile = (TextView)findViewById(R.id.displayNameProfile);
        insDisplayNameProfile.setText(user_id);

        insDisplayNameProfile = (TextView)findViewById(R.id.displayNameProfile);
        insStatusProfile = (TextView)findViewById(R.id.status_profile);
        insDisplayImageProfile = (ImageView) findViewById(R.id.displayImage_profile);
        sendRequestProfile = (Button) findViewById(R.id.sendFriendRequest_profile);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentState = "not_friends";

        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        insDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        insDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayname = dataSnapshot.child("name").getValue().toString();
                String displayStatus = dataSnapshot.child("status").getValue().toString();
                String imageUrl = dataSnapshot.child("image").getValue().toString();

                insDisplayNameProfile.setText(displayname);
                insStatusProfile.setText(displayStatus);
                if (!imageUrl.equals("default")){
                    Picasso.with(profileActivity.this).load(imageUrl).placeholder(R.drawable.ca).into(insDisplayImageProfile);
                }

                friendRequestRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("recieved")){
                                currentState = "req_recieved";
                                sendRequestProfile.setText("Accept Friend Request");
                                sendRequestProfile.setEnabled(true);
                            }
                            else if (req_type.equals("sent")){
                                currentState = "req_sent";
                                sendRequestProfile.setText("Cancel Friend Request");
                                sendRequestProfile.setEnabled(true);
                            }
                        }
                        else {
                            friendsRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)){
                                        currentState = "friends";
                                        sendRequestProfile.setText("Unfriend This Person");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendRequestProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestProfile.setEnabled(false);
                if(currentState.equals("not_friends")){
                    friendRequestRef.child(firebaseUser.getUid()).child(user_id).child("request_type").setValue("send").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                friendRequestRef.child(user_id).child(firebaseUser.getUid()).child("request_type").setValue("recieved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            currentState ="req_sent";
                                            sendRequestProfile.setText("Cancel Friend Request");
                                            Toast.makeText(profileActivity.this,"Friend Request Sent",Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            }else {
                                Toast.makeText(profileActivity.this,"Friend Request Not Sent",Toast.LENGTH_LONG).show();
                            }
                            sendRequestProfile.setEnabled(true);

                        }
                    });
                }

                if (currentState.equals("req_sent")){
                    friendRequestRef.child(firebaseUser.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                friendRequestRef.child(user_id).child(firebaseUser.getUid()).removeValue();
                                currentState = "not_friends";
                                sendRequestProfile.setText("Send Friend Request");
                            }
                            else {
                                Toast.makeText(profileActivity.this,task.getException().getMessage().toString(),Toast.LENGTH_LONG).show();
                            }
                            sendRequestProfile.setEnabled(true);
                        }
                    });
                }

                if (currentState.equals("req_recieved") ){
                    final String CurrentDate = DateFormat.getDateInstance().format(new Date());
                    friendsRef.child(firebaseUser.getUid()).child(user_id).setValue(CurrentDate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                friendsRef.child(user_id).child(firebaseUser.getUid()).setValue(CurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        friendRequestRef.child(firebaseUser.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    friendRequestRef.child(user_id).child(firebaseUser.getUid()).removeValue();
                                                    currentState = "friends";
                                                    sendRequestProfile.setText("Unfriend This Person");
                                                }
                                                else {
                                                    Toast.makeText(profileActivity.this,task.getException().getMessage().toString(),Toast.LENGTH_LONG).show();
                                                }
                                                sendRequestProfile.setEnabled(true);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }

                if (currentState.equals("friends")){
                    friendsRef.child(firebaseUser.getUid()).child(user_id).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        friendsRef.child(user_id).child(firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                friendRequestRef.child(firebaseUser.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            currentState = "not_friends";
                                                            sendRequestProfile.setText("Send Friend Request");
                                                        }
                                                        else {
                                                            Toast.makeText(profileActivity.this,task.getException().getMessage().toString(),Toast.LENGTH_LONG).show();
                                                        }
                                                        sendRequestProfile.setEnabled(true);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });
    }
}
