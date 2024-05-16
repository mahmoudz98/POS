package com.casecode.pos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.pos.databinding.ItemSubscriptionBinding
import timber.log.Timber

class SubscriptionAdapter(
    private val itemClick: (Subscription) -> Unit,
) :
    RecyclerView.Adapter<SubscriptionViewHolder>() {
    private var subscriptionItems: List<Subscription> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitSubscriptions(items: List<Subscription>) {
        subscriptionItems = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SubscriptionViewHolder {
        return SubscriptionViewHolder.from(parent)
    }

    override fun onBindViewHolder(
        holoder: SubscriptionViewHolder,
        position: Int,
    ) {
        if (itemCount == 0) return

        holoder.bind(subscriptionItems[position], itemClick)
    }

    override fun getItemCount(): Int {
        return subscriptionItems.size
    }
}

 class SubscriptionViewHolder(private val binding: ItemSubscriptionBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private lateinit var itemClick: (Subscription) -> Unit

    init
    {
        binding.apply {
            // TODO: custom to add configure with google play to pay subscription.
            btnSubscriptionPay.setOnClickListener {
                // it.visibility = View.INVISIBLE

                subscription?.run {
                    itemClick(this)
                }
            }
        }
    }

    fun bind(
        element: Subscription,
        itemClick: (Subscription) -> Unit,
    ) {
        Timber.e("element = $element")
        this.itemClick = itemClick
        binding.subscription = element
        if (bindingAdapterPosition == 0) {
            binding.btnSubscriptionPay.visibility = View.INVISIBLE
        }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): SubscriptionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemSubscriptionBinding.inflate(layoutInflater, parent, false)
            return SubscriptionViewHolder(binding)
        }
    }
}