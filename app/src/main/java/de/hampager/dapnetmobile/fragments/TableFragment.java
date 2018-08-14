package de.hampager.dapnetmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hampager.dap4j.DAPNET;
import de.hampager.dap4j.DapnetSingleton;
import de.hampager.dap4j.callbacks.DapnetListener;
import de.hampager.dap4j.callbacks.DapnetResponse;
import de.hampager.dap4j.models.CallSign;
import de.hampager.dap4j.models.News;
import de.hampager.dap4j.models.Node;
import de.hampager.dap4j.models.Rubric;
import de.hampager.dap4j.models.Transmitter;
import de.hampager.dap4j.models.TransmitterGroup;
import de.hampager.dap4j.models.User;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.adapters.NodeAdapter;
import de.hampager.dapnetmobile.adapters.RubricAdapter;
import de.hampager.dapnetmobile.adapters.RubricContentAdapter;
import de.hampager.dapnetmobile.adapters.SubscriberAdapter;
import de.hampager.dapnetmobile.adapters.TransmitterAdapter;
import de.hampager.dapnetmobile.adapters.TransmitterGroupAdapter;
import de.hampager.dapnetmobile.adapters.UserAdapter;

public class TableFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String TAG = "TableFragment";
    public static final String TT = "tableType";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipe;
    private DAPNET dapnet;
    private TableTypes selected = TableTypes.SUBSCRIBERS;

    public TableFragment() {
        // Empty constructor needed for android
    }

    public static TableFragment newInstance(TableTypes tableType) {
        TableFragment fragment = new TableFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(TT, tableType);
        fragment.setArguments(arguments);
        return fragment;
    }
    public static TableFragment newInstance(TableTypes tableType,String additionalInfo) {
        TableFragment fragment = new TableFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(TT, tableType);
        arguments.putString("AdditionalInfo",additionalInfo);
        fragment.setArguments(arguments);
        return fragment;
    }

    private void initViews(View v) {
        SubscriberAdapter adapter = new SubscriberAdapter(new ArrayList<CallSign>());
        recyclerView = (RecyclerView) v.findViewById(R.id.item_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        dapnet = DapnetSingleton.getInstance().getDapnet();
        switch (selected) {
            case CALLS:
                break;
            case SUBSCRIBERS:
                fetchSubscribers();
                break;
            case RUBRICS:
                fetchRubrics();
                break;
            case RUBRIC_CONTENT:
                fetchRubricContent();
                break;
            case TRANSMITTERS:
                fetchTransmitters();
                break;
            case TRANSMITTER_GROUPS:
                fetchTransmitterGroups();
                break;
            case NODES:
                fetchNodes();
                break;
            case USERS:
                fetchUsers();
                break;
        }
    }

    private void fetchSubscribers() {
        SubscriberAdapter adapter = new SubscriberAdapter(new ArrayList<CallSign>());
        recyclerView.setAdapter(adapter);
        mSwipe.setRefreshing(true);
        dapnet.getAllCallSigns(new DapnetListener<List<CallSign>>() {
            @Override
            public void onResponse(DapnetResponse<List<CallSign>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    // tasks available
                    List<CallSign> data = dapnetResponse.body();
                    Comparator<CallSign> comparator = new Comparator<CallSign>() {
                        @Override
                        public int compare(CallSign o1, CallSign o2) {
                            return o2.getName().compareTo(o1.getName());
                        }
                    };
                    Collections.sort(data, comparator);
                    adapter.setmValues(data);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Error");
                    //TODO: .code,.message, UI
                }
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable throwable) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, throwable.getMessage());
            }
        });

    }

    private void fetchRubrics() {
        RubricAdapter adapter = new RubricAdapter(new ArrayList<Rubric>());
        recyclerView.setAdapter(adapter);
        dapnet.getAllRubrics(new DapnetListener<List<Rubric>>() {
            @Override
            public void onResponse(DapnetResponse<List<Rubric>> dapnetResponse) {
                List<Rubric> data = dapnetResponse.body();
                Comparator<Rubric> comparator = new Comparator<Rubric>() {
                    @Override
                    public int compare(Rubric o1, Rubric o2) {
                        return o2.getName().compareTo(o1.getName());
                    }
                };
                Collections.sort(data, comparator);
                adapter.setmValues(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Major connection error");
            }
        });
    }

    private void fetchRubricContent() {
        RubricContentAdapter adapter = new RubricContentAdapter(new ArrayList<News>());
        recyclerView.setAdapter(adapter);
        dapnet.getNews("", new DapnetListener<List<News>>() {
            @Override
            public void onResponse(DapnetResponse<List<News>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    List<News> data = dapnetResponse.body();
                    Comparator<News> comparator = new Comparator<News>() {
                        @Override
                        public int compare(News o1, News o2) {
                            return o2.getTimestamp().compareTo(o1.getTimestamp());
                        }
                    };
                    Collections.sort(data, comparator);
                    adapter.setmValues(data);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Major connection error");
                News tnews = new News("Error connecting, content not yet implemented", "", 1);
                List<News> data = new ArrayList<>();
                data.add(tnews);
                adapter.setmValues(data);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchTransmitters() {
        TransmitterAdapter adapter = new TransmitterAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        dapnet.getAllTransmitters(new DapnetListener<List<Transmitter>>() {
            @Override
            public void onResponse(DapnetResponse<List<Transmitter>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    adapter.setmValues(dapnetResponse.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Major connection error");
            }
        });
    }

    private void fetchTransmitterGroups() {
        TransmitterGroupAdapter adapter = new TransmitterGroupAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        dapnet.getAllTransmitterGroups(new DapnetListener<List<TransmitterGroup>>() {
            @Override
            public void onResponse(DapnetResponse<List<TransmitterGroup>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    adapter.setmValues(dapnetResponse.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Major connection error");
            }
        });
    }

    private void fetchNodes() {
        NodeAdapter adapter = new NodeAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        dapnet.getAllNodes(new DapnetListener<List<Node>>() {
            @Override
            public void onResponse(DapnetResponse<List<Node>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    adapter.setmValues(dapnetResponse.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Major connection error");
            }
        });
    }

    private void fetchUsers() {
        UserAdapter adapter = new UserAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        dapnet.getAllUsers(new DapnetListener<List<User>>() {
            @Override
            public void onResponse(DapnetResponse<List<User>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    adapter.setmValues(dapnetResponse.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Major connection error");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = this.getArguments();
        if (arguments != null) {
            selected = (TableTypes) arguments.getSerializable(TT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_call, container, false);
        v.setTag(TAG);
        setHasOptionsMenu(true);
        mSwipe = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshCalls);
        initViews(v);
        // Setup refresh listener which triggers new data loading
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchSubscribers();
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "Creating menu...");
        inflater.inflate(R.menu.main_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        //TODO FIX filer, eg get Filter from adapter and filter query
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public enum TableTypes {CALLS, SUBSCRIBERS, RUBRICS, RUBRIC_CONTENT, TRANSMITTERS, TRANSMITTER_GROUPS, NODES, USERS}
}
