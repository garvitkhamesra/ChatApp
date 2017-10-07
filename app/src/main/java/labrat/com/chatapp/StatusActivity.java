package labrat.com.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar insToolbar;
    private EditText insStatus;
    private Button insUpdate;
    private DatabaseReference insDatabaseReference;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        insToolbar = (Toolbar) findViewById(R.id.status_navbar);
        setSupportActionBar(insToolbar);
        getSupportActionBar().setTitle("Status Update");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        insStatus = (EditText)findViewById(R.id.status_update);
        insUpdate = (Button) findViewById(R.id.statusUpdateButton);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        insDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        insUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = insStatus.getText().toString();

                if(!TextUtils.isEmpty(status)){
                    progressDialog = new ProgressDialog(StatusActivity.this);
                    progressDialog.setTitle("Updating Status");
                    progressDialog.setMessage("Saving Changes");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    insDatabaseReference.child("Status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.hide();
                                Intent intent = new Intent(StatusActivity.this,Account_Settings.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(StatusActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(StatusActivity.this, "Enter Status", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
