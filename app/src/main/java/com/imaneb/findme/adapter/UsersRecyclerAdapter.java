package com.imaneb.findme.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.firestore.DocumentSnapshot;
import com.imaneb.findme.R;
import com.imaneb.findme.data.model.User;
import com.imaneb.findme.data.repository.AuthRepository;
import com.imaneb.findme.data.repository.DatabaseRepository;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UsersRecyclerAdapter extends FirestoreRecyclerAdapter<User, UsersRecyclerAdapter.UserViewHolder> {

    private RequestManager requestManager;
    private UserListener userListener;
    private String CurrentUid;
    private DatabaseRepository databaseRepository;
    private CompositeDisposable disposable = new CompositeDisposable();
    private String constraint_gender = "All";
    AuthRepository authRepository;

    public UsersRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options, DatabaseRepository databaseRepository, RequestManager requestManager ) {
        super(options);
        this.requestManager = requestManager;
        this.databaseRepository = databaseRepository;
    }
    public void loadUserInfo(String currentUid) {
        CurrentUid = currentUid;
    }

    public void setClickListener(UserListener userListener){
        this.userListener = userListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
        databaseRepository.getUserinfo(CurrentUid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribe(new Observer<DocumentSnapshot>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        constraint_gender = user.getConstraints().get("constraint_gender").toString();
                        holder.nameView.setText(model.getDisplayName());
                        if (!(model.getGender().equals(constraint_gender) || constraint_gender.equals("All"))){
                            holder.fullLayout.setMaxHeight(0);
                            holder.fullLayout.setVisibility(View.GONE);
                        }
                        if (!model.getStatus().equals("default")) {
                            holder.statusView.setText(model.getStatus());
                        }
                        if (!model.getImage().equals("default")) {
                            requestManager.load(model.getImage()).into(holder.profileImage);
                        }
                        if (model.isOnline()) {
                            //holder.onlineView.setVisibility(View.VISIBLE);
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.users_view_item, parent, false);
        return new UserViewHolder(view);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ConstraintLayout fullLayout;
        public TextView nameView;
        public TextView statusView;
        public CircleImageView profileImage;
        public CircleImageView onlineView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            fullLayout = itemView.findViewById(R.id.full_layout);
            nameView = itemView.findViewById(R.id.display_name);
            statusView = itemView.findViewById(R.id.status);
            profileImage = itemView.findViewById(R.id.profile_image);
            onlineView = itemView.findViewById(R.id.online);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
            userListener.onUserClick(snapshot);
        }
    }

    public interface UserListener {
        void onUserClick(DocumentSnapshot documentSnapshot);
    }

}


