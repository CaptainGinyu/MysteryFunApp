package captainginyu.mysteryfunapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String ANONYMOUS = "anonymous";
    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    public static final String ADMIN_CHILD = "admin";
    public static final String LOGGEDIN_CHILD = "loggedin";
    public static final String BLOCKED_CHILD = "blocked";

    private GoogleApiClient googleApiClient;
    private ProgressBar progressBar;
    private Button sendButton;
    private EditText messageEditText;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String username;
    private String photoUrl;
    private DatabaseReference firebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>
            firebaseAdapter;

    private RecyclerView messageRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    private AlertDialog alertDialog;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) v.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) v.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) v.findViewById(R.id.messengerImageView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = ANONYMOUS;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(MESSAGES_CHILD)) {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API).build();

        if (firebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            username = firebaseUser.getDisplayName();
            if (firebaseUser.getPhotoUrl() != null) {
                photoUrl = firebaseUser.getPhotoUrl().toString();
            }
            firebaseDatabaseReference.child(LOGGEDIN_CHILD).push()
                    .setValue(new LoggedinUsers(firebaseUser.getEmail(),
                            firebaseUser.getDisplayName()));
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        sendButton = (Button) findViewById(R.id.sendButton);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        messageEditText.setCursorVisible(false);
        messageEditText.clearFocus();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentMessage = messageEditText.getText().toString();

                String patternString = "";

                for (int i = 0; i < Blacklist.bannedWords.length; i++) {
                    patternString += "(" + Blacklist.bannedWords[i] + ")";
                    if (i < Blacklist.bannedWords.length - 1) {
                        patternString += "|";
                    }
                }

                currentMessage = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE)
                        .matcher(currentMessage).replaceAll("*BLEEP*");
                final FriendlyMessage friendlyMessage = new
                        FriendlyMessage(currentMessage,
                        username, photoUrl, ServerValue.TIMESTAMP);

                firebaseDatabaseReference.child(BLOCKED_CHILD).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot blockedUser : dataSnapshot.getChildren()) {
                            if (((String) ((HashMap) blockedUser.getValue()).get("email"))
                                    .equals(firebaseUser.getEmail())) {
                                Toast.makeText(MainActivity.this,
                                        "An admin blocked you from posting",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        firebaseDatabaseReference.child(MESSAGES_CHILD)
                                .push().setValue(friendlyMessage);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                messageEditText.setText("");
            }
        });

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });



        messageRecyclerView = (RecyclerView) findViewById(R.id.messages);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);


        firebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.item_message,
                MessageViewHolder.class,
                firebaseDatabaseReference.child(MESSAGES_CHILD)) {

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder,
                                              FriendlyMessage friendlyMessage, int position) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                if (friendlyMessage.getText() != null) {
                    viewHolder.messageTextView.setText(friendlyMessage.getText());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                }

                viewHolder.messengerTextView.setText(friendlyMessage.getName() + ", "
                        + DateFormat.format("MM/dd/yyyy hh:mm:ss", new Date(
                                Long.parseLong(friendlyMessage.getTimestamp().toString()))));
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(
                            MainActivity.this,
                            R.mipmap.ic_account_circle_black));
                } else {
                    Glide.with(MainActivity.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }
            }
        };

        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    messageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        messageRecyclerView.setAdapter(firebaseAdapter);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Rect outRect = new Rect();
            messageEditText.getGlobalVisibleRect(outRect);
            if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                View v = getCurrentFocus();
                messageEditText.clearFocus();
                InputMethodManager imm = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                messageEditText.setCursorVisible(false);
            } else {
                messageEditText.requestFocus();
                messageEditText.setCursorVisible(true);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        firebaseDatabaseReference.child(ADMIN_CHILD)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot admin : dataSnapshot.getChildren()) {
                            String curr = admin.getValue().toString();
                            if (curr.equals(firebaseUser.getEmail())) {
                                menu.add(0, 1, 1, "Block");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                firebaseDatabaseReference.child(LOGGEDIN_CHILD)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot loggedinUser : dataSnapshot.getChildren()) {
                                    Object curr = loggedinUser.getValue();
                                    if (((HashMap) curr).get("email").equals(firebaseUser.getEmail())) {
                                        firebaseDatabaseReference.child(LOGGEDIN_CHILD)
                                                .child(loggedinUser.getKey()).removeValue();
                                    }
                                }

                                firebaseAuth.signOut();
                                Auth.GoogleSignInApi.signOut(googleApiClient);
                                username = ANONYMOUS;

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            case 1:
                firebaseDatabaseReference.child(BLOCKED_CHILD)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<String> alreadyBlocked = new ArrayList<String>();
                        for (DataSnapshot blockedUser : dataSnapshot.getChildren()) {
                            alreadyBlocked.add(
                                    (String) ((HashMap) blockedUser.getValue()).get("email"));
                        }
                        firebaseDatabaseReference.child(LOGGEDIN_CHILD)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        ArrayList<String> itemsList = new ArrayList<String>();
                                        final ArrayList<String> nameList = new ArrayList<String>();
                                        final ArrayList<String> emailList = new ArrayList<String>();
                                        for (DataSnapshot loggedinUser : dataSnapshot.getChildren()) {
                                            HashMap value = (HashMap) loggedinUser.getValue();
                                            if (!alreadyBlocked.contains((String) value.get("email"))) {
                                                itemsList.add(
                                                        ((String) value.get("name")) + " (" +
                                                                ((String) value.get("email")) + ")");
                                                emailList.add((String) value.get("email"));
                                                nameList.add((String) value.get("name"));
                                            }
                                        }

                                        AlertDialog.Builder alertDialogBuilder =
                                                new AlertDialog.Builder(MainActivity.this);
                                        if (itemsList.size() > 0) {
                                            final String[] itemsArray =
                                                    itemsList.toArray(
                                                            new String[itemsList.size()]);
                                            alertDialogBuilder.setItems(itemsArray,
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface,
                                                                            int i) {
                                                            firebaseDatabaseReference.child(BLOCKED_CHILD)
                                                                    .push().setValue(
                                                                    new LoggedinUsers(emailList.get(i),
                                                                            nameList.get(i)));
                                                        }
                                                    });
                                        }

                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.setTitle("Block a user from posting");
                                        alertDialog.show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseDatabaseReference.child(LOGGEDIN_CHILD)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot loggedinUser : dataSnapshot.getChildren()) {
                    Object curr = loggedinUser.getValue();
                    if (((HashMap) curr).get("email").equals(firebaseUser.getEmail())) {
                        firebaseDatabaseReference.child(LOGGEDIN_CHILD)
                                .child(loggedinUser.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
