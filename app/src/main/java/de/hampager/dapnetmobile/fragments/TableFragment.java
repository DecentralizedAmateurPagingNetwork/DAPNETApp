package de.hampager.dapnetmobile.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

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
import de.hampager.dapnetmobile.activites.MainActivity;
import de.hampager.dapnetmobile.adapters.NodeAdapter;
import de.hampager.dapnetmobile.adapters.RubricAdapter;
import de.hampager.dapnetmobile.adapters.RubricContentAdapter;
import de.hampager.dapnetmobile.adapters.SubscriberAdapter;
import de.hampager.dapnetmobile.adapters.TransmitterAdapter;
import de.hampager.dapnetmobile.adapters.TransmitterGroupAdapter;
import de.hampager.dapnetmobile.adapters.UserAdapter;

public class TableFragment extends Fragment implements SearchView.OnQueryTextListener {
    //TODO: Fix Class format, extract Tabletypes properly
    private static final String TAG = "TableFragment";
    public static final String TT = "tableType";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipe;
    private DAPNET dapnet;
    private TableTypes selected = TableTypes.SUBSCRIBERS;
    private String addInfo ="";
    private RecyclerView.Adapter currentAdapter;
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
        SubscriberAdapter adapter = new SubscriberAdapter(new ArrayList<>());
        recyclerView = v.findViewById(R.id.item_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        dapnet = DapnetSingleton.getInstance().getDapnet();
        //Set titlebar
        MainActivity activity=((MainActivity) getActivity());
        activity.setActionBarTitle("DAPNET " + selected.toString().toLowerCase());
        switch (selected) {
            case CALLS:
                break;
            case SUBSCRIBERS:
                mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // Your code to refresh the list here.
                        // Make sure you call swipeContainer.setRefreshing(false)
                        // once the network request has completed successfully.
                        fetchSubscribers();
                    }
                });
                fetchSubscribers();
                break;
            case RUBRICS:
                mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fetchRubrics();
                    }
                });
                fetchRubrics();
                break;
            case RUBRIC_CONTENT:
                mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fetchRubricContent();
                    }
                });
                fetchRubricContent();
                break;
            case TRANSMITTERS:
                mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fetchTransmitters();
                    }
                });
                fetchTransmitters();
                break;
            case TRANSMITTER_GROUPS:
                mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fetchTransmitterGroups();
                    }
                });
                fetchTransmitterGroups();
                break;
            case NODES:
                mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fetchNodes();
                    }
                });
                fetchNodes();
                break;
            case USERS:
                mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fetchUsers();
                    }
                });
                fetchUsers();
                break;
        }
    }

    private void fetchSubscribers() {
        SubscriberAdapter adapter = new SubscriberAdapter(new ArrayList<>());
        currentAdapter=adapter;
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
                            String n1="Z";
                            String n2="Z";
                            if (o1!=null&&o1.getName()!=null)
                                n1=o1.getName();
                            if (o2!=null&&o2.getName()!=null)
                                n2=o2.getName();
                            return n2.compareTo(n1);
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
                mSwipe.setRefreshing(false);
            }
        });

    }

    private void fetchRubrics() {
        mSwipe.setRefreshing(true);
        RubricAdapter adapter = new RubricAdapter(new ArrayList<>());
        currentAdapter=adapter;
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
                mSwipe.setRefreshing(false);

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Major connection error");
                mSwipe.setRefreshing(false);

            }
        });
    }

    private void fetchRubricContent() {
        mSwipe.setRefreshing(true);
        RubricContentAdapter adapter = new RubricContentAdapter(new ArrayList<>());
        currentAdapter=adapter;
        recyclerView.setAdapter(adapter);
        dapnet.getNews(addInfo, new DapnetListener<List<News>>() {
            @Override
            public void onResponse(DapnetResponse<List<News>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    List<News> data = dapnetResponse.body();
                    Comparator<News> comparator = new Comparator<News>() {
                        @Override
                        public int compare(News o1, News o2) {
                            String o1compareValue = "0";
                            String o2compareValue = "0";
                            if (o1 != null && o1.getTimestamp() != null) {
                                o1compareValue = o1.getTimestamp();
                            }
                            if (o2 != null && o2.getTimestamp() != null) {
                                o2compareValue = o2.getTimestamp();
                            }
                            return o1compareValue.compareTo(o2compareValue);
                        }
                    };
                    Collections.sort(data, comparator);
                    adapter.setmValues(data);
                    adapter.notifyDataSetChanged();
                }
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Major connection error");
                List<News> data = new ArrayList<>();
                adapter.setmValues(data);
                adapter.notifyDataSetChanged();
                mSwipe.setRefreshing(false);

            }
        });
    }

    private void fetchTransmitters() {
        mSwipe.setRefreshing(true);
        TransmitterAdapter adapter = new TransmitterAdapter(new ArrayList<>());
        currentAdapter=adapter;
        recyclerView.setAdapter(adapter);
        dapnet.getAllTransmitters(new DapnetListener<List<Transmitter>>() {
            @Override
            public void onResponse(DapnetResponse<List<Transmitter>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    adapter.setmValues(dapnetResponse.body());
                    adapter.notifyDataSetChanged();
                }
                mSwipe.setRefreshing(false);

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Major connection error");
                mSwipe.setRefreshing(false);

            }
        });
    }

    private void fetchTransmitterGroups() {
        mSwipe.setRefreshing(true);
        TransmitterGroupAdapter adapter = new TransmitterGroupAdapter(new ArrayList<>());
        currentAdapter=adapter;
        recyclerView.setAdapter(adapter);
        dapnet.getAllTransmitterGroups(new DapnetListener<List<TransmitterGroup>>() {
            @Override
            public void onResponse(DapnetResponse<List<TransmitterGroup>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    adapter.setmValues(dapnetResponse.body());
                    adapter.notifyDataSetChanged();
                }
                mSwipe.setRefreshing(false);

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Major connection error");
                mSwipe.setRefreshing(false);

            }
        });
    }

    private void fetchNodes() {
        mSwipe.setRefreshing(true);
        NodeAdapter adapter = new NodeAdapter(new ArrayList<>());
        currentAdapter=adapter;
        recyclerView.setAdapter(adapter);
        dapnet.getAllNodes(new DapnetListener<List<Node>>() {
            @Override
            public void onResponse(DapnetResponse<List<Node>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    adapter.setmValues(dapnetResponse.body());
                    adapter.notifyDataSetChanged();
                }
                mSwipe.setRefreshing(false);

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Major connection error");
                mSwipe.setRefreshing(false);

            }
        });
    }

    private void fetchUsers() {
        mSwipe.setRefreshing(true);
        UserAdapter adapter = new UserAdapter(new ArrayList<>());
        currentAdapter=adapter;
        recyclerView.setAdapter(adapter);
        dapnet.getAllUsers(new DapnetListener<List<User>>() {
            @Override
            public void onResponse(DapnetResponse<List<User>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    adapter.setmValues(dapnetResponse.body());
                    adapter.notifyDataSetChanged();
                }
                mSwipe.setRefreshing(false);

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Major connection error");
                mSwipe.setRefreshing(false);

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = this.getArguments();
        if (arguments != null) {
            selected = (TableTypes) arguments.getSerializable(TT);
            try{
                addInfo = arguments.getString("AdditionalInfo");
            }catch (Exception e){
                addInfo = "";
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_call, container, false);
        v.setTag(TAG);
        setHasOptionsMenu(true);
        mSwipe = v.findViewById(R.id.swipeRefreshCalls);
        initViews(v);
        // Setup refresh listener which triggers new data loading

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "Creating menu...");
        inflater.inflate(R.menu.main_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        //TODO FIX filer, eg get Filter from adapter and filter query
        //HACK
        boolean res=true;
        if(currentAdapter!=null){
            switch (selected) {
                case CALLS:
                    res=false;
                    break;
                case SUBSCRIBERS:
                    SubscriberAdapter subscriberAdapter=(SubscriberAdapter) currentAdapter;
                    subscriberAdapter.getFilter().filter(query);
                    break;
                case RUBRICS:
                    RubricAdapter rubricAdapter=(RubricAdapter) currentAdapter;
                    rubricAdapter.getFilter().filter(query);
                    break;
                case RUBRIC_CONTENT:
                    RubricContentAdapter rubricContentAdapter=(RubricContentAdapter) currentAdapter;
                    rubricContentAdapter.getFilter().filter(query);
                    break;
                case TRANSMITTERS:
                    TransmitterAdapter transmitterAdapter=(TransmitterAdapter) currentAdapter;
                    transmitterAdapter.getFilter().filter(query);
                    break;
                case TRANSMITTER_GROUPS:
                    TransmitterGroupAdapter transmitterGroupAdapter=(TransmitterGroupAdapter) currentAdapter;
                    transmitterGroupAdapter.getFilter().filter(query);
                    break;
                case NODES:
                    NodeAdapter nodeAdapter=(NodeAdapter) currentAdapter;
                    nodeAdapter.getFilter().filter(query);
                    break;
                case USERS:
                    UserAdapter userAdapter=(UserAdapter) currentAdapter;
                    userAdapter.getFilter().filter(query);
                    break;
                default:
                    res=false;
            }
        }
        return res;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public enum TableTypes {CALLS, SUBSCRIBERS, RUBRICS, RUBRIC_CONTENT, TRANSMITTERS, TRANSMITTER_GROUPS, NODES, USERS}
}
