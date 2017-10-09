package labrat.com.chatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar insToolbar;
    private ViewPager insViewPager;
    private SectionAdapter sectionAdapter;
    private TabLayout insTabLayout;
    private DatabaseReference insUserRef;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        insToolbar = (Toolbar) findViewById(R.id.mainActivity_navbar);
        setSupportActionBar(insToolbar);
        getSupportActionBar().setTitle("Chatter");

        insViewPager = (ViewPager) findViewById(R.id.tab_pager);
        sectionAdapter = new SectionAdapter(getSupportFragmentManager());
        insViewPager.setAdapter(sectionAdapter);

        insTabLayout = (TabLayout) findViewById(R.id.main_tab);
        insTabLayout.setupWithViewPager(insViewPager);

        insUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null){
            ChoiceIntent();
        }
        else if (!firebaseUser.isEmailVerified()){
            ChoiceIntent();
        }
        else{
            insUserRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        insUserRef.child("online").setValue(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()== R.id.main_logout){
            FirebaseAuth.getInstance().signOut();
            ChoiceIntent();
        }
        else if (item.getItemId() == R.id.main_accountSettings){
            Intent intent = new Intent(MainActivity.this, Account_Settings.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.main_AllUsers){
            Intent AllUsers = new Intent(MainActivity.this, AllUsers.class);
            startActivity(AllUsers);
        }
        return true;
    }

    private void ChoiceIntent(){
        Intent SignUpChoice = new Intent(MainActivity.this, SignUpChoice.class);
        startActivity(SignUpChoice);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
