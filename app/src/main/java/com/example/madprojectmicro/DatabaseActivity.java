package com.example.madprojectmicro;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class DatabaseActivity extends AppCompatActivity {
    private static final String TAG = "DatabaseActivity";
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton homeButton;
    private sqlitemanager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        // Initialize database manager
        dbManager = sqlitemanager.getInstance(this);

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        homeButton = findViewById(R.id.homeButton);

        // Set home button icon if it's missing
        if (homeButton != null) {
            homeButton.setImageResource(android.R.drawable.ic_menu_revert); // Use a default home icon
            homeButton.setOnClickListener(v -> finish()); // Go back to dashboard
        } else {
            Log.e(TAG, "Home button not found in layout!");
        }

        // Set up ViewPager with tab layout using FragmentStateAdapter
        setupViewPager();

        // Set up tabs
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Customers");
                    break;
                case 1:
                    tab.setText("Hotel Rooms");
                    break;
            }
        }).attach();
    }

    private void setupViewPager() {
        try {
            // Use FragmentStateAdapter instead of RecyclerView.Adapter
            DatabasePagerAdapter pagerAdapter = new DatabasePagerAdapter(this);
            viewPager.setAdapter(pagerAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error in setupViewPager: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error setting up tabs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Fragment for Customer tab
    public static class CustomerFragment extends Fragment {
        private RecyclerView recyclerView;
        private TextView emptyView;
        private CustomerAdapter adapter;
        private List<Customer> customerList = new ArrayList<>();
        private sqlitemanager dbManager;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.tab_customer, container, false);

            recyclerView = view.findViewById(R.id.customerRecyclerView);
            emptyView = view.findViewById(R.id.emptyCustomerView);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new CustomerAdapter(customerList);
            recyclerView.setAdapter(adapter);

            dbManager = sqlitemanager.getInstance(getContext());
            loadCustomerData();

            return view;
        }

        private void loadCustomerData() {
            dbManager.getAllCustomers(new sqlitemanager.CustomerListCallback() {
                @Override
                public void onSuccess(List<Customer> customers) {
                    if (isAdded()) { // Check if fragment is still attached to activity
                        customerList.clear();
                        customerList.addAll(customers);
                        adapter.notifyDataSetChanged();

                        if (customers.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    if (isAdded()) {
                        Toast.makeText(getContext(),
                                "Failed to load customer data: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    // Fragment for Hotel tab
    public static class HotelFragment extends Fragment {
        private RecyclerView recyclerView;
        private TextView emptyView;
        private HotelAdapter adapter;
        private List<Hotel> hotelList = new ArrayList<>();
        private sqlitemanager dbManager;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.tab_hotel, container, false);

            recyclerView = view.findViewById(R.id.hotelRecyclerView);
            emptyView = view.findViewById(R.id.emptyHotelView);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new HotelAdapter(hotelList);
            recyclerView.setAdapter(adapter);

            dbManager = sqlitemanager.getInstance(getContext());
            loadHotelData();

            return view;
        }

        private void loadHotelData() {
            dbManager.getAllHotels(new sqlitemanager.HotelListCallback() {
                @Override
                public void onSuccess(List<Hotel> hotels) {
                    if (isAdded()) { // Check if fragment is still attached to activity
                        hotelList.clear();
                        hotelList.addAll(hotels);
                        adapter.notifyDataSetChanged();

                        if (hotels.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    if (isAdded()) {
                        Toast.makeText(getContext(),
                                "Failed to load hotel data: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    // ViewPager adapter using FragmentStateAdapter
    private static class DatabasePagerAdapter extends FragmentStateAdapter {
        public DatabasePagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new CustomerFragment();
                case 1:
                    return new HotelFragment();
                default:
                    return new CustomerFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2; // Two tabs: Customers and Hotel Rooms
        }
    }
}