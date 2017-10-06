package labrat.com.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null){
            Intent SignUpChoice = new Intent(MainActivity.this, SignUpChoice.class);
            startActivity(SignUpChoice);
            finish();
        }
    }
}
