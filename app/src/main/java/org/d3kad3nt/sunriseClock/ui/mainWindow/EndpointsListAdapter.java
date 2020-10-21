package org.d3kad3nt.sunriseClock.ui.mainWindow;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.d3kad3nt.sunriseClock.databinding.EndpointListElementBinding;
import org.d3kad3nt.sunriseClock.model.endpoint.EndpointConfig;


public class EndpointsListAdapter extends ListAdapter<EndpointConfig, EndpointsListAdapter.ViewHolder> {

    public EndpointsListAdapter() {
        super(new EndpointDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(EndpointListElementBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EndpointConfig light = getItem(position);
        holder.bind(createOnClickListener(light.getId()), light);
        holder.itemView.setTag(light);
    }

    private View.OnClickListener createOnClickListener(long endpointID) {
        return v -> Navigation.findNavController(v).navigate(
                EndpointsFragmentDirections.actionEndpointsToEndpointInfo(endpointID));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private EndpointListElementBinding binding;

        ViewHolder(@NonNull EndpointListElementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(View.OnClickListener listener, EndpointConfig item) {
            binding.setClickListener(listener);
            binding.setEndpoint(item);
            binding.executePendingBindings();
        }
    }

    static class EndpointDiffCallback extends DiffUtil.ItemCallback<EndpointConfig> {

        @Override
        public boolean areItemsTheSame(@NonNull EndpointConfig oldItem, @NonNull EndpointConfig newItem) {
            //TODO: use real UUID
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull EndpointConfig oldItem, @NonNull EndpointConfig newItem) {
            return oldItem == newItem;
        }
    }
}
