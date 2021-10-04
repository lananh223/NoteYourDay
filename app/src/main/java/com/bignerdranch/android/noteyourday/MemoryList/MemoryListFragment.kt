package com.bignerdranch.android.noteyourday.MemoryList

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.noteyourday.Memory
import com.bignerdranch.android.noteyourday.R
import java.util.*

private const val TAG = "MemoryListFragment"

class MemoryListFragment:Fragment() {

    /**
     * Required interface for hosting activities
     */
    interface Callbacks{
        fun onMemorySelected(memoryId: UUID)
    }

    private var callbacks: Callbacks?= null

    private lateinit var memoryRecyclerView: RecyclerView
    private var adapter: MemoryAdapter?= MemoryAdapter(emptyList())

    private val memoryListViewModel:MemoryListViewModel by lazy {
        ViewModelProviders.of(this).get(MemoryListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks =  context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_memory_list,container,false)

        memoryRecyclerView = view.findViewById(R.id.memory_recycler_view) as RecyclerView
        memoryRecyclerView.layoutManager = LinearLayoutManager(context)
        memoryRecyclerView.adapter = adapter
        registerForContextMenu(memoryRecyclerView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        memoryListViewModel.memoryListLiveData.observe(
            viewLifecycleOwner,
            Observer { memories ->
                memories?.let{
                    Log.i(TAG, "Got memories${memories.size}")
                    if (it.isNotEmpty()) {
                        // Set the new memory onTop
                        val memoryList = it.toMutableList()
                        memoryList.reverse()
                        updateUI(memoryList)
                    }
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks =  null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_memory_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.new_memory -> {
                val memory = Memory()
                memoryListViewModel.addMemory(memory)
                callbacks?.onMemorySelected(memory.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                val memory = adapter!!.memories[adapter!!.clickPosition]
                memoryListViewModel.deleteMemory(memory)
                true
            }
            else -> return super.onContextItemSelected(item)
        }
    }

    private fun updateUI(memories: List<Memory>) {
        adapter = MemoryAdapter(memories)
        memoryRecyclerView.adapter = adapter
    }

    private inner class MemoryHolder(view:View):RecyclerView.ViewHolder(view),
        View.OnClickListener, View.OnCreateContextMenuListener {

        private lateinit var memory:Memory
        private var titleTextView: TextView = itemView.findViewById(R.id.memory_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.memory_date)

        init{
            view.setOnClickListener(this)
            view.setOnCreateContextMenuListener(this)
        }

        fun bind(memory: Memory){
            this.memory = memory
            titleTextView.text = this.memory.detail
            dateTextView.text = this.memory.date.toString()
        }

        override fun onClick(v: View?) {
            callbacks?.onMemorySelected(memory.id)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            requireActivity().menuInflater.inflate(R.menu.context_menu, menu)
        }
    }

    private inner class MemoryAdapter(var memories:List<Memory>):RecyclerView.Adapter<MemoryHolder>(){

        var clickPosition = -1
        // create a new ViewHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryHolder {
            val view = layoutInflater.inflate(R.layout.list_item_memory,parent,false)
            return MemoryHolder(view)
        }

        override fun getItemCount() = memories.size
        // associate a ViewHolder with data
        override fun onBindViewHolder(holder: MemoryHolder, position: Int) {
            val memory = memories[position]
            holder.bind(memory)
            holder.itemView.setOnLongClickListener {
                clickPosition = holder.absoluteAdapterPosition
                false
            }
        }

        override fun onViewRecycled(holder: MemoryHolder) {
            holder.itemView.setOnLongClickListener(null)
            super.onViewRecycled(holder)
        }
    }
}