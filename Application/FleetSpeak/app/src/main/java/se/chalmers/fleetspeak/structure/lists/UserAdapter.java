package se.chalmers.fleetspeak.structure.lists;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.Model;
import se.chalmers.fleetspeak.model.ModelFactory;
import se.chalmers.fleetspeak.model.User;
import se.chalmers.fleetspeak.truck.TruckModeHandlerFactory;

/**
 * Created by David Gustafsson on 2015-08-01.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Model model;

    private Context context;
    private final LayoutInflater inflater;
    List<User> userList = Collections.emptyList();
    private boolean truckstate;

    private UserList.OnUserClickedListener onUserClickedListener;

    public UserAdapter(Context context){
        this.model = ModelFactory.getCurrentModel();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.truckstate = TruckModeHandlerFactory.getCurrentHandler().truckModeActive();
        List<User> users = model.getUsers(model.getCurrentRoom());
        if(users != null) {
            this.userList = users;
        }
    }

    public void setOnUserClickedListener(UserList.OnUserClickedListener listener){
        this.onUserClickedListener = listener;
    }

    public void UserClicked(int position){
        if(userList.get(position) != null) {
            Log.d("UserAdapter", " User clicked");
            if(onUserClickedListener != null){
                onUserClickedListener.onUserClicked(userList.get(position));
            }
        }
    }
    public void changeTruckState(boolean b){
        truckstate = b;
    }
    public void notifyDataSetChanged(boolean b){
        changeTruckState(b);
        notifyDataSetChanged();
    };
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.user_item_row, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(view, truckstate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User current = userList.get(position);
        holder.iv.setImageResource(R.drawable.ic_user);
        holder.username.setText(current.getName());
        ViewGroup.LayoutParams params;

        //Valid since we retrieve the instance of the communicator via the inverse casting.
        if(truckstate){
            float car = context.getResources().getDimension(R.dimen.carsize);
            holder.username.setTextSize(car);
        }else{
            float normal = context.getResources().getDimension(R.dimen.normalsize);
            holder.username.setTextSize(normal);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    public void resetList(List<User> list){
        if(list != null){
            userList = list;
        }else{
            userList = Collections.emptyList();
        }
        notifyDataSetChanged();
    }
    public void addItem(User user){
        userList.add(user);
        notifyItemInserted(userList.size()-1);
    }
    public void addItem(User user , int pos){
        userList.add(pos, user);
        notifyItemInserted(pos);
    }
    public void deleteItem(User user){
       int pos = userList.indexOf(user);
       if(pos >= 0){
           deleteItem(pos);
       }
    }
    public void deleteItem(int pos){
        userList.remove(pos);
        notifyItemRemoved(pos);
    }
    public User acessItem(int pos){
        if(!userList.isEmpty() && pos >= 0 && pos < getItemCount()){
           return userList.get(pos);
        }
        else
            return null;
    }


    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView iv;
        private TextView username;
        public UserViewHolder(View itemView, boolean b) {
            super(itemView);
            itemView.setOnClickListener(this);
            iv = (ImageView)itemView.findViewById(R.id.userItemIcon);

            username = (TextView) itemView.findViewById(R.id.userName);
        }
        @Override
        public void onClick(View view) {
                UserClicked(getAdapterPosition());
            }
        }
}

