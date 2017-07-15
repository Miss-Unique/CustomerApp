package com.example.soumyaagarwal.customerapp.CustomerLogin;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.soumyaagarwal.customerapp.Model.Customer;
import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.tablayout.Tabs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;

public class CustomerLogin extends AppCompatActivity {

    EditText username, password;
    Button button;
    String Username,Password;
    DatabaseReference database;
    CustomerSession session;
    TextInputLayout input_email, input_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        session = new CustomerSession(getApplicationContext());
        if(session.isolduser()==true)
        {
            goToTabLayout();
        }
        username = (EditText) findViewById(R.id.editText2);
        password = (EditText) findViewById(R.id.editText3);
        button = (Button) findViewById(R.id.login);
        input_email = (TextInputLayout)findViewById(R.id.input_emaillogin);
        input_password = (TextInputLayout)findViewById(R.id.input_passwordlogin);
        database = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Customer").getRef();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Username = username.getText().toString().trim();
                Password = password.getText().toString().trim();

                if (TextUtils.isEmpty(Username)) {
                    input_email.setError("Enter Email");
                    if (input_email.requestFocus()) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }

                if (TextUtils.isEmpty(Password)) {
                    input_password.setError("Enter Password");
                    if (input_password.requestFocus()) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                    }
                }

                if(!TextUtils.isEmpty(Username) && !TextUtils.isEmpty(Password)){
                    login();
                }
                else
                    Toast.makeText(getBaseContext(),"Enter Complete Details", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void login() {

        DatabaseReference db = database.child(Username).getRef();

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    Customer customer = dataSnapshot.getValue(Customer.class);
                    if (!customer.getPassword().equals(Password)) {
                        Toast.makeText(getBaseContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        session.create_oldusersession(Username);
                        Intent intent = new Intent(CustomerLogin.this, Tabs.class);
                        intent.putExtra("page",0);
                        startActivity(intent);
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Customer Not Registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void goToTabLayout()
    {
        Intent intent = new Intent(CustomerLogin.this, Tabs.class);
        intent.putExtra("page",0);
        startActivity(intent);
        finish();

    }
}