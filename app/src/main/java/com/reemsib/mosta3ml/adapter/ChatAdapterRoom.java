//package com.reemsib.mosta3ml.adapter;
//
//import android.app.Activity;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.reemsib.mosta3ml.R;
//import com.reemsib.mosta3ml.model.ChatMessage;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//
//public class ChatAdapterRoom extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
// //   User user = GlobalSettings.get_my_user();
//   // String Imag_other;
//    List<ChatMessage> ChatRoomModules_;
//    private final int VIEW_TYPE_TEXT_MY = 0;
//    private final int DATE_FLAG = 0;
//    private final int VIEW_TYPE_TEXT_FRIEND = 3;
//
//    RecyclerView rv;
//    private Activity activity;
//   // String imageURL;
//    String userId;
//
//    public ChatAdapterRoom(Activity activity, List<ChatMessage> ChatRoomModules_) {
//        this.activity = activity;
//        this.ChatRoomModules_ = ChatRoomModules_;
//     //   this.Imag_other = Imag_other;
////        this.fmessageModelList = fmessageModelList;
//
//    }
//
//
////    public void chat_ref(List<ChatRoomModules> ChatRoomModules_1) {
////        ChatRoomModules_.clear();
////        ChatRoomModules_ = ChatRoomModules_1;
////        this.notifyDataSetChanged();
////
////
////    }
//
//    public void chat_ref(List<ChatMessage> ChatRoomModules_1) {
//        //ChatRoomModules_.clear();
//        this.ChatRoomModules_ = ChatRoomModules_1;
//        Log.e("ChatRoomModules_",ChatRoomModules_.size()+"");
//        this.notifyDataSetChanged();
//    }
//
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//
//        if (viewType == VIEW_TYPE_TEXT_MY) {
//
//            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sent_message, viewGroup, false);
//            return new MyMessageTextViewHolder(view);
//        }
//        else if (viewType == VIEW_TYPE_TEXT_FRIEND) {
//
//            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recieved_message, viewGroup, false);
//            return new FriendMessageTextViewHolder(view);
//      }
////        else if (viewType == DATE_FLAG) {
////            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_date_view, viewGroup, false);
////            return new DateViewHolder(view);
////        }
//        return null;
//    }
//
//
//    @Override
//    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
////
//////        System.out.println("Log holder date " + fmessageModelList.get(position).date);
////
//
//        holder.setIsRecyclable(false);
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
////        String s= formatter.format(ChatRoomModules_.get(position).getTimestamp());
//
////
//
//        if (holder instanceof MyMessageTextViewHolder) {
//            MyMessageTextViewHolder myMessageViewHolder = (MyMessageTextViewHolder) holder;
//
//
////            if (ChatRoomModules_.get(position).getContent().equals("❤")) {
////                myMessageViewHolder.message.setText(ChatRoomModules_.get(position).getContent() + "");
////                myMessageViewHolder.message.setTextSize(50);
////            } else {
////                myMessageViewHolder.message.setText(ChatRoomModules_.get(position).getContent() + "");
////
////            }
////            ((MyMessageTextViewHolder) holder).img_cam.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                }
////            });
//
//         //   myMessageViewHolder.date.setText(ChatRoomModules_.get(position).getCreated_at());
//            if (ChatRoomModules_.get(position).getType().equals("0")){
//                myMessageViewHolder.message.setVisibility(View.VISIBLE);
//             //   myMessageViewHolder.img_cam.setVisibility(View.GONE);
//                myMessageViewHolder.message.setText(ChatRoomModules_.get(position).getMessage());
//
//            }else {
//                myMessageViewHolder.message.setVisibility(View.GONE);
//              //  myMessageViewHolder.img_cam.setVisibility(View.VISIBLE);
//
//                Log.e("myMessageViewHolder",ChatRoomModules_.get(position).getMessage());
//             //   Picasso.get().load(ChatRoomModules_.get(position).getMessage()).config(Bitmap.Config.RGB_565).into(myMessageViewHolder.img_cam);
//                try {
//
//
//                }catch (Exception e){
//
//                }
//
//            }
//
//            try {
//
//                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = fmt.parse(ChatRoomModules_.get(position).getCreated_at());
//                SimpleDateFormat fstr = new SimpleDateFormat("EEEE.HH:mm",Locale.ENGLISH);
//
//                String datestr= fstr.format(date);
//
//                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//                cal.setTime(date);
//
//
//                //String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", cal).toString();
////                myMessageViewHolder.date.setText(datestr);
//               // myMessageViewHolder.date.setText(cal.get(Calendar.DAY_OF_WEEK) + "." +cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE) );
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e("Exception",e.toString());
//            }
//
//
//
//        } else if (holder instanceof FriendMessageTextViewHolder) {
//            FriendMessageTextViewHolder friendMessageViewHolder = (FriendMessageTextViewHolder) holder;
//
//            if (ChatRoomModules_.get(position).getType().equals("0")){
//                friendMessageViewHolder.message.setVisibility(View.VISIBLE);
//               // friendMessageViewHolder.img_cam.setVisibility(View.GONE);
//                friendMessageViewHolder.message.setText(ChatRoomModules_.get(position).getMessage());
//
//            }else {
//                friendMessageViewHolder.message.setVisibility(View.GONE);
//             //   friendMessageViewHolder.img_cam.setVisibility(View.VISIBLE);
//
//                Log.e("myMessageViewHolder",ChatRoomModules_.get(position).getMessage());
//
//                try {
//                //    Picasso.get().load(ChatRoomModules_.get(position).getMessage()).config(Bitmap.Config.RGB_565).into(friendMessageViewHolder.img_cam);
//
//                }catch (Exception e){
//
//                }
//
//            }
//
//
////            if (ChatRoomModules_.get(position).getType().equals("0")){
////                friendMessageViewHolder.message.setVisibility(View.VISIBLE);
//////                friendMessageViewHolder.img_cam.setVisibility(View.GONE);
////                friendMessageViewHolder.message.setText(ChatRoomModules_.get(position).getMessage());
////
////            }else {
////                friendMessageViewHolder.message.setVisibility(View.GONE);
//////                friendMessageViewHolder.img_cam.setVisibility(View.VISIBLE);
////                try {
////                    Picasso.get().load(ChatRoomModules_.get(position).getMessage()).into(friendMessageViewHolder.img_cam);
////
////                }catch (Exception e){
////
////                }
////            }
//////            friendMessageViewHolder.date.setText(ChatRoomModules_.get(position).getCreatedAt());
////             // friendMessageViewHolder.message.setText(ChatRoomModules_.get(position).getMessage());
////
////            try {
////                Picasso.get().load(ChatRoomModules_.get(position).getMessage()).into(friendMessageViewHolder.img_cam);
////
////
//////                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//////                Date date = fmt.parse(ChatRoomModules_.get(position).getCreated_at());
//////                SimpleDateFormat fstr = new SimpleDateFormat("EEEE.HH:mm",Locale.ENGLISH);
//////
//////                String datestr= fstr.format(date);
////
////
////
////                //String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", cal).toString();
////              //  friendMessageViewHolder.date.setText(cal.get(Calendar.D) + "." +cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE) );
////              //  friendMessageViewHolder.date.setText(datestr);
////
////
////            } catch (Exception e) {
////                Log.e("Exception",e.toString());
////                e.printStackTrace();
////            }
//
////            try {
////
////                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
////               // cal.setTimeInMillis(Long.valueOf(ChatRoomModules_.get(position).getTimestamp()));
////                String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", cal).toString();
//////                friendMessageViewHolder.date.setText(date + "");
////               // friendMessageViewHolder.date.setText(GloableSettings.convert_time(activity,date.toString()));
////
////
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////
////            try{
////                Picasso.with(activity.getApplicationContext()).load(ChatRoomModules_.get(position).getUser_image()).into(friendMessageViewHolder.profileImage);
////
////            }catch (Exception e){
////
////            }
//
//
////            String time = fmessageModelList.get(position).date;
//////          System.out.println("Log FriendMessageTextViewHolder "+time);
////            time = time.substring(time.indexOf(" "), time.length());
////            time = time.substring(0, time.lastIndexOf(":"));
////            friendMessageViewHolder.friendTimeTxt.setText(time);
////
//        }
//
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return ChatRoomModules_.size();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        try {
//        //    Log.e("ChatRoomModules_",ChatRoomModules_.get(position).getSenderId()+"++++"+user.getId()+"");
////            if (ChatRoomModules_.get(position).getSenderId().equals(user.getId()+"")) {
////                return VIEW_TYPE_TEXT_MY;
////            } else {
////                return VIEW_TYPE_TEXT_FRIEND;
////            }
//
//
//        } catch (Exception e) {
////            if (ChatRoomModules_.get(position).getSenderId().equals(user.getId()+"")) {
////                return VIEW_TYPE_TEXT_MY;
////
////            } else {
////                return VIEW_TYPE_TEXT_FRIEND;
////
////            }
//     }
//
//
//        return position % 2 == 0 ? VIEW_TYPE_TEXT_MY : VIEW_TYPE_TEXT_FRIEND;
//
//    }
//
//    class FriendMessageTextViewHolder extends RecyclerView.ViewHolder {
//        //
//       //LinearLayout friendMessageLY;
//        //CircleImageView profileImage;
//       //TextView friendTimeTxt;
//        TextView message;
//       // DismissibleImageView img_cam ;
//        TextView date;
//
//        public FriendMessageTextViewHolder(View itemView) {
//            super(itemView);
//
//         //   message = itemView.findViewById(R.id.received_text);
//       //     date = itemView.findViewById(R.id.received_time);
//          //  profileImage = itemView.findViewById(R.id.received_image);
//          //  img_cam = itemView.findViewById(R.id.received_cam);
////            friendTimeTxt = itemView.findViewById(R.id.friendTimeTxt);
////            friendMessageTxt = itemView.findViewById(R.id.friendMessageTxt);
//        }
//    }
//
//
//    class MyMessageTextViewHolder extends RecyclerView.ViewHolder {
//        TextView message;
//        TextView date;
//      //  CircleImageView profileImage;
//        //  TextView friendTimeTxt;
//  //      DismissibleImageView img_cam;
//
//
//        public MyMessageTextViewHolder(View itemView) {
//            super(itemView);
//
//        //    message = itemView.findViewById(R.id.sent_text);
//        //    img_cam = itemView.findViewById(R.id.send_cam);
//        //    date = itemView.findViewById(R.id.sent_time);
//            /*profileImage = itemView.findViewById(R.id.profileImage);*/
//
//           /* itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });*/
//        }
//    }
//
////    class DateViewHolder extends RecyclerView.ViewHolder {
////
////        TextView date;
////
////        public DateViewHolder(View itemView) {
////
////            super(itemView);
////            date = itemView.findViewById(R.id.date_view);
////        }
////    }
//
//    public ChatAdapterRoom getAdapter() {
//        return this;
//    }
//
//
//
//}
//
