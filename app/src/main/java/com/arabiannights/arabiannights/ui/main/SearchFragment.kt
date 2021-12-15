
package com.arabiannights.arabiannights.ui.main

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.database.FirebaseRepository
import com.arabiannights.arabiannights.modals.FoodModal
import com.arabiannights.arabiannights.utils.constants.SEARCHLOG
import com.arabiannights.arabiannights.viewholders.ItemRowViewHolder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.*

class SearchFragment : Fragment() {

    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val repo = FirebaseRepository()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // recycler
        searchRecyclerView.adapter = adapter
        searchRecyclerView.setOnClickListener {
            hideKeyboard()
        }
        shimmerSearchContainer.setOnClickListener{
            hideKeyboard()
        }
        searchLayout.setOnClickListener {
            hideKeyboard()
        }

        // edit text
        searchButton_search.setOnClickListener {
            val query = searchText_search.text.toString().toLowerCase().trim()
            hideKeyboard()
            if(query.isNotEmpty()){
                adapter.clear()
                setView(View.VISIBLE, View.INVISIBLE)
                GlobalScope.launch(Dispatchers.IO) {
                    async {
                        val items = repo.searchItem(query)
                        withContext(Dispatchers.Main){
                            Log.d(SEARCHLOG,"Search : ${items}")
                            refreshItems(items)
                        }
                    }
                }
            }
        }
        searchText_search.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let{value->
                    if(value.isEmpty()){
                        adapter.clear()
                        status_search.visibility = View.VISIBLE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


    }


    private fun refreshItems(items : List<FoodModal>){
        adapter.clear()
        context?.let {c->
            if(items.isEmpty()){
                setView(View.INVISIBLE, View.VISIBLE)
            }else{
                setView(View.INVISIBLE, View.INVISIBLE)
                items.forEach {
                    adapter.add(ItemRowViewHolder(requireFragmentManager(),c,it))
                }
            }
        }

    }

    private fun setView(sVis : Int, tVis:Int){
        shimmerSearchContainer.visibility = sVis
        status_search.visibility = tVis
    }

    private fun hideKeyboard(){
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(imm != null){
            imm.hideSoftInputFromWindow(view?.windowToken,0)
        }
    }

}