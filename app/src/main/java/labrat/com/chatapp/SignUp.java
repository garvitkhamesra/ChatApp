package labrat.com.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    private EditText insUsername;
    private EditText insEmail;
    private EditText insPassword;
    private Button insSignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        insUsername = (EditText) findViewById(R.id.signup_username);
        insEmail = (EditText) findViewById(R.id.signup_email);
        insPassword = (EditText) findViewById(R.id.signup_password);
        insSignUp = (Button) findViewById(R.id.signup_button);

        insSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = insUsername.getText().toString();
                String email = insEmail.getText().toString();
                String password = insPassword.getText().toString();

                signup(username,email,password);
            }
        });
    }

    private void signup(String username, String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignUp.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(SignUp.this, "Some error", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
}
