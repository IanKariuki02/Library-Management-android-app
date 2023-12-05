package com.iiitnr.libraryapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MyBookAdapter extends RecyclerView.Adapter<MyBookAdapter.Book_ViewHolder> {
    private List<MyBook> myBookList =new ArrayList<>();

    public MyBookAdapter(List<MyBook> myBookList) {

        this.myBookList = myBookList;
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

    @NonNull
    @Override
    public Book_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.see_my_book_view,viewGroup,false);
        return new Book_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Book_ViewHolder bookViewHolder, int i) {

        bookViewHolder.bookId1.setText("ID : "+ myBookList.get(i).getBid());
        bookViewHolder.bookName1.setText("Title : "+ myBookList.get(i).getTitle());
        bookViewHolder.bookType1.setText("Type : "+ myBookList.get(i).getType());
        bookViewHolder.bookIdate.setText("Issue Date : "+simpleDateFormat.format(myBookList.get(i).getIdate()));
        bookViewHolder.bookDdate.setText("Due Date : "+simpleDateFormat.format(myBookList.get(i).getDdate()));

    }

    @Override
    public int getItemCount() {
        return myBookList.size();
    }

    class Book_ViewHolder extends RecyclerView.ViewHolder
    {
        TextView bookName1,bookId1,bookType1,bookIdate,bookDdate;

        public Book_ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookId1=(TextView) itemView.findViewById(R.id.bookId1);
            bookIdate=itemView.findViewById(R.id.bookIdate);
            bookName1=itemView.findViewById(R.id.bookName1);
            bookType1=itemView.findViewById(R.id.bookType1);
            bookDdate=itemView.findViewById(R.id.bookDdate);
        }
    }


}
