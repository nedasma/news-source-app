package com.example.newssourceapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newssourceapp.R
import com.example.newssourceapp.databinding.FragmentNewsListBinding
import com.example.newssourceapp.ui.MainActivity
import com.example.newssourceapp.ui.adapter.NewsListItemAdapter
import com.example.newssourceapp.ui.viewmodel.NewsListViewModel
import com.example.newssourceapp.ui.viewmodel.NewsListViewModel.UiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TOTAL_PAGE_COUNT = 20

/**
 *
 */
class NewsListFragment : Fragment() {

    private var _binding: FragmentNewsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NewsListViewModel
    private var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        viewModel.load()

        val adapter = NewsListItemAdapter()
        binding.newsList.adapter = adapter
        binding.newsList.layoutManager = LinearLayoutManager(context)
        binding.newsList.addOnScrollListener(this@NewsListFragment.scrollListener)
        adapter.setOnItemClickListener {
            val bundle = Bundle().apply { putSerializable("article", it) }
            findNavController().navigate(
                R.id.action_NewsListFragment_to_NewsDetailsFragment,
                bundle
            )
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest { state ->
                    when (state) {
                        is UiState.Success -> {
                            adapter.differ.submitList(state.data.toList())
                        }
                        is UiState.Error -> {
                            Snackbar.make(view, state.error, Snackbar.LENGTH_SHORT).show()
                        }
                        else -> {
                            // do nothing, as the data is loading
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // The scroll listener allows pagination of queries, so that once the end of the list is reached,
    // the VM is informed that a new query with pagination parameter needs to be requested and returned
    // back to the view
    private var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            isScrolling = (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val isLastItem = layoutManager.findFirstVisibleItemPosition() + layoutManager.childCount >= layoutManager.itemCount
            val isNotFirstItem = layoutManager.findFirstVisibleItemPosition() >= 0
            val isTotalMoreThanVisible = layoutManager.itemCount >= TOTAL_PAGE_COUNT

            val paginate = isLastItem && isNotFirstItem && isTotalMoreThanVisible && isScrolling
            if (paginate) {
                viewModel.load(setIncrementer = true)
                isScrolling = false
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewsListFragment()
    }
}