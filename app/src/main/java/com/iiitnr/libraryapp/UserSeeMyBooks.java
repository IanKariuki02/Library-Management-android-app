package com.iiitnr.libraryapp;

import android.app.ProgressDialog;

import android.os.Bundle;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserSeeMyBooks extends AppCompatActivity {


    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private TextView ifNoBook1;
    private User user = new User();
    private Book book = new Book();
    private ProgressDialog progressDialog;


    List<Integer> userBooksArrayList = new ArrayList<Integer>();
    MyBook myBook=new MyBook();
    RecyclerView.Adapter adapter;
    public int i;

    private List<MyBook> userBooks =new ArrayList<>();


    RecyclerView recyclerView;
    //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_see_my_books);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ifNoBook1 = (TextView) findViewById(R.id.ifNoBook1);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
        recyclerView=(RecyclerView)findViewById(R.id.recycle1) ;
        progressDialog.show();
        showBook();

        adapter=new MyBookAdapter(userBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getParent()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void setBook(int i, boolean isLastBookInList)
    {
        db.document("Book/"+ user.getBook().get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                book =task.getResult().toObject(Book.class);

                Date issueDate= user.getDate().get(i).toDate();
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(issueDate);
                calendar.add(Calendar.DAY_OF_MONTH,14);
                Date deadlineDate=calendar.getTime();

                userBooks.add(new MyBook(book.getId(), book.getTitle(), book.getType(), issueDate, deadlineDate));
                if(isLastBookInList) {
                    System.out.println("Updating RecycleView Adapter");
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    private void showBook()
    {
        db.document("User/" + firebaseAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {


                    user = task.getResult().toObject(User.class);

                    assert user != null;
                    if (!user.getBook().isEmpty()) {

                        userBooksArrayList = user.getBook();
                        for (i = 0; i < userBooksArrayList.size(); i++) {

                            setBook(i, i== userBooksArrayList.size()-1);
                        }
                        progressDialog.cancel();

                    }
                    else
                    {
                        progressDialog.cancel();
                        ifNoBook1.setText("YOU HAVE NO ISSUED BOOKS !");
                        ifNoBook1.setTextSize(18);
                    }
                }
            }
        });

    }

}