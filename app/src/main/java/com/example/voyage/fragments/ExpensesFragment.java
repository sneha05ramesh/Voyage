package com.example.voyage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.R;
import com.example.voyage.adapters.ExpenseAdapter;
import com.example.voyage.models.Expense;
import com.example.voyage.network.ApiClientExpense;
import com.example.voyage.network.ExpenseApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpensesFragment extends Fragment {

    private RecyclerView expensesRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        expensesRecyclerView = view.findViewById(R.id.expensesRecyclerView);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchExpenses();

        return view;
    }

    private void fetchExpenses() {
        ExpenseApi api = ApiClientExpense.getClient().create(ExpenseApi.class);
        api.getExpenses().enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    expensesRecyclerView.setAdapter(new ExpenseAdapter(response.body()));
                } else {
                    Toast.makeText(getContext(), "Failed to load expenses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
