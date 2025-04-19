package com.example.voyage.network;

import com.example.voyage.models.Expense;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ExpenseApi {
    @GET("expenses.json")
    Call<List<Expense>> getExpenses();
}
