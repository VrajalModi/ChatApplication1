package com.chatapp.firebaseproject.chatapplication;

import android.app.Notification;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessagelist;
    private DatabaseReference mUserDatabase;
    private String mCurrentUser;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_LEFT = 0;


    public MessageAdapter(List<Messages> mMessagelist) {
        this.mMessagelist = mMessagelist;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == MSG_TYPE_LEFT)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_layout_left, viewGroup, false);
            return new MessageViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_layout_right, viewGroup, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {

//        mAuth = FirebaseAuth.getInstance();
//        String current_user_id = mAuth.getCurrentUser().getUid();

        Messages c = mMessagelist.get(i);
  //      String from_user = c.getFrom();
        String message_type = c.getType();
        long message_time =  Long.parseLong(String.valueOf(c.getTime()));
     //   String message_seen = c.getSeen();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy(hh:mm:ss)");
        String dateString = formatter.format(new Date(message_time));
        messageViewHolder.timeText.setText(dateString);
      //  messageViewHolder.seenText.setText("Seen");

//        if (message_seen.equals("true")) {
//            messageViewHolder.seenText.setText("S");
//        }
//        else
//        {
//            messageViewHolder.seenText.setText("D");
//        }



//        if (from_user.equals(current_user_id))
//        {
//            messageViewHolder.messageText.setBackgroundColor(Color.WHITE);
//            messageViewHolder.messageText.setTextColor(Color.BLACK);
//        }
//        else
//        {
//            messageViewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
//            messageViewHolder.messageText.setTextColor(Color.WHITE);
//        }
//        messageViewHolder.messageText.setText(c.getMessage());
//
//
//        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);
//
//        mUserDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                String name = dataSnapshot.child("name").getValue().toString();
//                String image = dataSnapshot.child("thumb_image").getValue().toString();
//
//            //    messageViewHolder.displayName.setText(name);
//
//            //    Picasso.get().load(image)
//             //           .placeholder(R.drawable.om).into(messageViewHolder.profileImage);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        if(message_type.equals("text")) {

            messageViewHolder.messageText.setText(c.getMessage());
           // messageViewHolder.messageText.setText(Html.fromHtml(convertToHtml(c.getMessage()) + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;")); // 10 spaces
       //     messageViewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            messageViewHolder.messageText.setVisibility(View.INVISIBLE);
          //  Picasso.get().load(c.getMessage())
           //         .placeholder(R.drawable.om).into(messageViewHolder.messageImage);

        }
    }


    @Override
    public int getItemCount() {
        return mMessagelist.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
//        public CircleImageView profileImage;
//        public TextView displayName;
//        public ImageView messageImage;
        public TextView timeText;
     //   public TextView seenText;


        public MessageViewHolder(@NonNull View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.show_message);
//            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
       //     seenText = (TextView) view.findViewById(R.id.show_seen);
//            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            timeText = (TextView)view.findViewById(R.id.show_time);

        }
    }

    @Override
    public int getItemViewType(int position) {

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (!mMessagelist.get(position).getFrom().equals(mCurrentUser))
        {
            return MSG_TYPE_LEFT;
        }
        else
        {
            return MSG_TYPE_RIGHT;
        }
    }
}
