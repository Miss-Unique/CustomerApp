package com.example.soumyaagarwal.customerapp.chat;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.soumyaagarwal.customerapp.CustomerLogin.CustomerSession;
import com.example.soumyaagarwal.customerapp.Model.ChatListModel;
import com.example.soumyaagarwal.customerapp.Model.NameAndStatus;
import com.example.soumyaagarwal.customerapp.MyProfile.ContactCoordinator;
import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.adapter.chatListAdapter;
import com.example.soumyaagarwal.customerapp.helper.DividerItemDecoration;
import com.example.soumyaagarwal.customerapp.listener.ClickListener;
import com.example.soumyaagarwal.customerapp.listener.RecyclerTouchListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;

public class ChatHome extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View myFragmentView;
    FragmentManager fmm;
    ArrayList<ChatListModel> list = new ArrayList<>();
    ArrayList<ChatListModel> b = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseReference dbChatList;
    private String mykey;
    private chatListAdapter mAdapter;
    private HashMap<DatabaseReference, ValueEventListener> dbLastMessageHashMap = new HashMap<>();
    private ChildEventListener dbChatCHE;
    private HashMap<DatabaseReference, ValueEventListener> dbProfileRefHashMap = new HashMap<>();
    SwipeRefreshLayout swipeLayout;
    FloatingActionButton chat_add;

    public static ChatHome newInstance() {
        ChatHome Home = new ChatHome();
        return Home;
    }

    public static ChatHome newInstance(Bundle args) {
        ChatHome fragment = new ChatHome();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_chats, container, false);
        setHasOptionsMenu(true);
        return myFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fmm = getFragmentManager();

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        chat_add = (FloatingActionButton) getView().findViewById(R.id.add_chat);
        CustomerSession coordinatorSession = new CustomerSession(getActivity());
        mykey = coordinatorSession.getUsername();
        dbChatList = DBREF.child("Users").child("Userchats").child(mykey).getRef();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        mAdapter = new chatListAdapter(list, getActivity());
        recyclerView.setAdapter(mAdapter);
        b = list;

        LoadData();

        chat_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ContactCoordinator.class));
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ChatListModel topic = b.get(position);
                final Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("dbTableKey", topic.getDbTableKey());
                intent.putExtra("otheruserkey", topic.getUserkey());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        final MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (newText.equals(""))
                {
                    mAdapter = new chatListAdapter(list, getActivity());
                    recyclerView.setAdapter(mAdapter);
                    b = list;
                }
                else
                {
                    final ArrayList<ChatListModel> filteredModelList = filter(list, newText);
                    mAdapter = new chatListAdapter(filteredModelList, getContext());
                    recyclerView.setAdapter(mAdapter);
                    b= filteredModelList;
                }

                return true;
            }
        });
        searchView.onActionViewCollapsed();
        searchView.setIconifiedByDefault(true);
        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        mAdapter.setFilter(list);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    private ArrayList<ChatListModel> filter(ArrayList<ChatListModel> models, String query) {
        query = query.toLowerCase();
        final ArrayList<ChatListModel> filteredModelList = new ArrayList<>();
        for (ChatListModel model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void LoadData() {

        dbChatCHE = dbChatList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    final String dbTablekey = dataSnapshot.getValue(String.class);
                    final String otheruserkey = dataSnapshot.getKey();
                    final DatabaseReference dbLastMsg = DBREF.child("Chats").child(dbTablekey).child("lastMsg").getRef();

                    ValueEventListener dbLastMsgChildEventListener = dbLastMsg.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                boolean alreadyexists = false;
                                for (ChatListModel chatListModel : list) {
                                    if (chatListModel.getDbTableKey().equals(dbTablekey)) {
                                        list.remove(chatListModel);
                                        chatListModel.setLastMsg(dataSnapshot.getValue(Long.class));
                                        list.add(chatListModel);
                                        sortChatList();
                                        alreadyexists = true;
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                if (!alreadyexists) {
                                    final Long lastMsgId = dataSnapshot.getValue(Long.class);
                                    DatabaseReference dbProfileRef = DBREF.child("Users").child("Usersessions").child(otheruserkey).getRef();

                                    ValueEventListener valueEventListener = dbProfileRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                boolean alreadyexists = false;
                                                NameAndStatus user = dataSnapshot.getValue(NameAndStatus.class);
                                                for (ChatListModel chatListModel : list) {
                                                    if (chatListModel.getUserkey().equals(dataSnapshot.getKey())) {
                                                        alreadyexists = true;
                                                        break;
                                                    }

                                                }
                                                if (!alreadyexists) {
                                                    ChatListModel chatListModel = new ChatListModel(user.getName(), otheruserkey, dbTablekey, getRandomMaterialColor("400"), lastMsgId);
                                                    list.add(chatListModel);
                                                    sortChatList();
                                                    mAdapter.notifyDataSetChanged();

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    dbProfileRefHashMap.put(dbProfileRef, valueEventListener);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    dbLastMessageHashMap.put(dbLastMsg, dbLastMsgChildEventListener);

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Iterator<HashMap.Entry<DatabaseReference, ValueEventListener>> iterator = dbLastMessageHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<DatabaseReference, ValueEventListener> entry = (HashMap.Entry<DatabaseReference, ValueEventListener>) iterator.next();
            if (entry.getValue() != null)
                entry.getKey().removeEventListener(entry.getValue());
        }
        Iterator<HashMap.Entry<DatabaseReference, ValueEventListener>> iterator2 = dbProfileRefHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<DatabaseReference, ValueEventListener> entry = (HashMap.Entry<DatabaseReference, ValueEventListener>) iterator2.next();
            if (entry.getValue() != null) entry.getKey().removeEventListener(entry.getValue());
        }
        if (dbChatCHE != null)
            dbChatList.removeEventListener(dbChatCHE);
        mAdapter.removeListeners();

    }

    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getActivity().getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    private void sortChatList() {
        Collections.sort(list, new Comparator<ChatListModel>() {
            @Override
            public int compare(ChatListModel o1, ChatListModel o2) {
                return o1.getLastMsg() > o2.getLastMsg() ? -1 : 0;
            }
        });
    }

    @Override
    public void onRefresh() {
        list.clear();
        LoadData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }
}