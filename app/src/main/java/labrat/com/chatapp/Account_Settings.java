package labrat.com.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account_Settings extends AppCompatActivity {

    private DatabaseReference insDatabaseReference;
    private FirebaseUser insFirebaseUser;
    private TextView insStaus;
    private TextView insName;
    private CircleImageView insImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account__settings);

        insName = (TextView) findViewById(R.id.displayNameSettings);
        insStaus = (TextView) findViewById(R.id.statusMessage);
        insImage = (CircleImageView) findViewById(R.id.displayPictureSettings);

        insFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = insFirebaseUser.getUid();
        insDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        insDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("Status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                insName.setText(name);
                insStaus.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
