package com.example.voyage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.R;
import com.example.voyage.models.Expense;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.GroupedExpenseViewHolder> {

    private final List<String> dateKeys = new ArrayList<>();
    private final Map<String, List<Expense>> groupedExpenses = new LinkedHashMap<>();

    public ExpenseAdapter(List<Expense> expenses) {
        for (Expense e : expenses) {
            groupedExpenses.computeIfAbsent(e.date, k -> new ArrayList<>()).add(e);
        }
        dateKeys.addAll(groupedExpenses.keySet());
    }

    @NonNull
    @Override
    public GroupedExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense_grouped, parent, false);
        return new GroupedExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupedExpenseViewHolder holder, int position) {
        String date = dateKeys.get(position);
        List<Expense> expensesForDate = groupedExpenses.get(date);

        holder.dateHeader.setText(date);
        double total = 0;
        holder.expenseContainer.removeAllViews();

        for (Expense e : expensesForDate) {
            total += e.amount;

            View expenseItem = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_expense, holder.expenseContainer, false);

            ((TextView) expenseItem.findViewById(R.id.tvMerchant)).setText(e.merchant);
            ((TextView) expenseItem.findViewById(R.id.tvCategory)).setText(e.category);
            ((TextView) expenseItem.findViewById(R.id.tvAmount)).setText("$" + e.amount);
            ((TextView) expenseItem.findViewById(R.id.tvDate)).setText(e.date);
            ((TextView) expenseItem.findViewById(R.id.tvLocation)).setText(e.location);

            holder.expenseContainer.addView(expenseItem);
        }

        holder.totalAmount.setText("Total: $" + String.format("%.2f", total));
    }

    @Override
    public int getItemCount() {
        return dateKeys.size();
    }

    static class GroupedExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView dateHeader, totalAmount;
        LinearLayout expenseContainer;

        GroupedExpenseViewHolder(View itemView) {
            super(itemView);
            dateHeader = itemView.findViewById(R.id.tvDateHeader);
            totalAmount = itemView.findViewById(R.id.tvTotal);
            expenseContainer = itemView.findViewById(R.id.expenseContainer);
        }
    }
}
