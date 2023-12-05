package com.iiitnr.libraryapp;

import android.app.ProgressDialog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdminIssueBook extends AppCompatActivity {


    private TextInputLayout editCardNo1, editBid3;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private boolean res1, res2;
    private User user = new User();
    private Book book = new Book();

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_issue_book);
        FirebaseApp.initializeApp(this);
        Button issueButton = (Button) findViewById(R.id.issueButton);
        editBid3 = (TextInputLayout) findViewById(R.id.editBid3);
        editCardNo1 = (TextInputLayout) findViewById(R.id.editCardNo1);
        db=FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        issueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIssueBookProcess();
            }
        });
    }


    //Check if user exists, then inner function will check if book exists too
    private void startIssueBookProcess() {

        Log.d("abc","invoked");
        if (verifyBid() | verifyCard()) {
            Toast.makeText(AdminIssueBook.this, "Check entries", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Please Wait !");
        progressDialog.show();
        getUser();
    }

    private boolean verifyBid() {
        String t = editBid3.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            editBid3.setErrorEnabled(true);
            editBid3.setError("Book Id Required");
            return true;
        } else {
            editBid3.setErrorEnabled(false);
            return false;
        }
    }

    private boolean verifyCard() {
        String t = editCardNo1.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            editCardNo1.setErrorEnabled(true);
            editCardNo1.setError("Card No. Required");
            return true;
        } else {
            editCardNo1.setErrorEnabled(false);
            return false;
        }
    }


    private boolean getUser() {
        db.collection("User").whereEqualTo("card", Integer.parseInt(editCardNo1.getEditText().getText().toString().trim())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    if (task.getResult().size() == 1) {
                        res1 = true;
                        for (QueryDocumentSnapshot doc : task.getResult())
                            user = doc.toObject(User.class);
                        System.out.println("User found");
                        getBook();
                    } else {

                        res1 = false;
                        progressDialog.cancel();
                        Toast.makeText(AdminIssueBook.this, "No Such User !", Toast.LENGTH_SHORT).show();
                        System.out.println("No Such User");
                    }
                } else {
                    res1 = false;
                    progressDialog.cancel();
                    Toast.makeText(AdminIssueBook.this, "Try Again !", Toast.LENGTH_SHORT).show();
                    System.out.println("Firebase 'getUser' request unsuccessful");
                }


            }
        });

        return res1;
    }

    private boolean getBook() {

        db.document("Book/" + Integer.parseInt(editBid3.getEditText().getText().toString().trim())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        res2 = true;
                        book = task.getResult().toObject(Book.class);
                        System.out.println("Book found");
                        issueBook();
                    } else {
                        res2 = false;
                        progressDialog.cancel();
                        Toast.makeText(AdminIssueBook.this, "No Such Book !", Toast.LENGTH_SHORT).show();
                        System.out.println("No Such Book");
                    }
                } else {
                    res2 = false;
                    progressDialog.cancel();
                    Toast.makeText(AdminIssueBook.this, "Try Again !", Toast.LENGTH_SHORT).show();
                    System.out.println("Firebase 'getBook' request unsuccessful");
                }
            }
        });

        return res2;
    }

    private void issueBook() {
        if (user.getBook().size() >= 5) {
            progressDialog.cancel();
            Toast.makeText(AdminIssueBook.this, "User Already Has 5 books issued !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (book.getAvailable() == 0) {
            progressDialog.cancel();
            Toast.makeText(AdminIssueBook.this, "No Units of this Book Available !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (book.getUnit().contains(Integer.parseInt(editBid3.getEditText().getText().toString().trim()) % 100)) {
            progressDialog.cancel();
            Toast.makeText(AdminIssueBook.this, "This Unit is Already Issued !", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Integer> l = new ArrayList<Integer>();
        l = user.getBook();
        l.add(Integer.parseInt(editBid3.getEditText().getText().toString().trim()));
        user.setBook(l);
        l = user.getFine();
        l.add(0);
        user.setFine(l);
        l = user.getRe();
        l.add(1);
        user.setRe(l);
        List<Timestamp> l1 = new ArrayList<>();
        l1 = user.getDate();
        Calendar c = new Calendar() {
            @Override
            protected void computeTime() {

            }

            @Override
            protected void computeFields() {

            }

            @Override
            public void add(int field, int amount) {

            }

            @Override
            public void roll(int field, boolean up) {

            }

            @Override
            public int getMinimum(int field) {
                return 0;
            }

            @Override
            public int getMaximum(int field) {
                return 0;
            }

            @Override
            public int getGreatestMinimum(int field) {
                return 0;
            }

            @Override
            public int getLeastMaximum(int field) {
                return 0;
            }
        };
        c=Calendar.getInstance();
        Date d = c.getTime();
        Timestamp t = new Timestamp(d);
        l1.add(t);
        user.setDate(l1);
        db.document("User/" + user.getEmail()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    book.setAvailable(book.getAvailable()-1);
                    List<Integer> l1=new ArrayList<>();
                    l1= book.getUnit();
                    l1.add(Integer.parseInt(editBid3.getEditText().getText().toString().trim()) % 100);
                    book.setUnit(l1);

                    db.document("Book/" + book.getId()).set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.cancel();
                                Toast.makeText(AdminIssueBook.this, "Book Issued Successfully !", Toast.LENGTH_SHORT).show();

                            } else {
                                progressDialog.cancel();
                                Toast.makeText(AdminIssueBook.this, "Try Again !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.cancel();
                    Toast.makeText(AdminIssueBook.this, "Try Again !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
