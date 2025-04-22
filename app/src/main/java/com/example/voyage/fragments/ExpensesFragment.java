package com.example.voyage.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.R;
import com.example.voyage.adapters.ExpenseAdapter;
import com.example.voyage.models.Expense;
import com.example.voyage.response.TripPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExpensesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView totalTextView;
    private ExpenseAdapter adapter;
    private TripPlan tripPlan;

    public static ExpensesFragment newInstance(TripPlan tripPlan) {
        ExpensesFragment fragment = new ExpensesFragment();
        Bundle args = new Bundle();
        args.putSerializable("trip_plan", tripPlan);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.expenseRecyclerView);
        totalTextView = view.findViewById(R.id.totalExpenseTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter();
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            tripPlan = (TripPlan) getArguments().getSerializable("trip_plan");
            fetchExpenses(tripPlan.getId());
        }
    }

    private void fetchExpenses(String itineraryId) {
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Expense> allExpenses = new ArrayList<>();
        int[] total = {0};

        db.collection("users")
                .document(uid)
                .collection("itineraries")
                .document(itineraryId)
                .collection("flights")
                .get()
                .addOnSuccessListener(query -> {
                    for (QueryDocumentSnapshot doc : query) {
                        String name = doc.getString("airline");
                        Long price = doc.getLong("price");
                        if (name != null && price != null) {
                            allExpenses.add(new Expense("Flight", name, price.intValue()));
                            total[0] += price.intValue();
                        }
                    }

                    db.collection("users")
                            .document(uid)
                            .collection("itineraries")
                            .document(itineraryId)
                            .collection("hotels")
                            .get()
                            .addOnSuccessListener(hotelQuery -> {
                                for (QueryDocumentSnapshot doc : hotelQuery) {
                                    String name = doc.getString("name");
                                    Long price = doc.getLong("price");
                                    if (name != null && price != null) {
                                        allExpenses.add(new Expense("Hotel", name, price.intValue()));
                                        total[0] += price.intValue();
                                    }
                                }

                                adapter.setExpenses(allExpenses);
                                totalTextView.setText("Total: $" + total[0]);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load expenses", Toast.LENGTH_SHORT).show();
                    Log.e("ExpensesFragment", "Error loading expenses", e);
                });
    }
}
