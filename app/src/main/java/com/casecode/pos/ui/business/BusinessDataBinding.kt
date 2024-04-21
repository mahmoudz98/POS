package com.casecode.pos.ui.business

import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.model.users.Branch
import com.casecode.pos.R
import com.casecode.pos.adapter.AutoCompleteAdapter
import com.casecode.pos.adapter.BranchesAdapter
import com.casecode.pos.adapter.SubscriptionAdapter
import timber.log.Timber

@BindingAdapter("items")
fun AppCompatAutoCompleteTextView.setAutoCompleteItems(items: Array<String>?) {
    Timber.e("items $items")
    if (items != null) {
        val adapter =
            AutoCompleteAdapter(
                context,
                items.toList(),
            )
        setOnItemClickListener { _, _, position, _ ->
            adapter.setSelectedItem(position)
            setText(adapter.getItem(position), false)
        }
        setAdapter(adapter)
    }
}

/**
 * When there is no  data (data is null), hide the [RecyclerView], otherwise show it.
 */
@BindingAdapter("bindListSubscriptions")
fun RecyclerView.bindListSubscriptions(items: List<Subscription>?) {
    items?.let {
        (adapter as SubscriptionAdapter).submitSubscriptions(items)
    }
}

@BindingAdapter("bindCost", "bindDuration")
fun TextView.bindCostAndDuration(
    cost: Long,
    duration: Long,
) {
    val durationMonth = duration / 30
    val costAndDuration =
        context.getString(R.string.subscription_price_currency) +
            " $cost / $durationMonth${context.getString(R.string.subscription_price_month)} "
    text = costAndDuration
}

@BindingAdapter("itemsBranch")
fun RecyclerView.bindListBranch(items: ArrayList<Branch>?) {
    Timber.i("size of items in branches = ${items?.size}")

    items?.let {
        (adapter as BranchesAdapter).submitList(items)
    }
}