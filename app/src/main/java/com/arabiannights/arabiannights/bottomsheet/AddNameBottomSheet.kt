package com.arabiannights.arabiannights.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.database.FirebaseRepository
import com.arabiannights.arabiannights.modals.UserModal
import com.arabiannights.arabiannights.utils.Singleton
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.bottomsheet_add_name.*
import kotlinx.coroutines.*

class AddNameBottomSheet(val user : UserModal) : BottomSheetDialogFragment() {

    private val repo = FirebaseRepository()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottomsheet_add_name, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return v
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogThemeNoFloating)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set name
        nameText_name.setText(user.name)


        // save name
        submitButton_name.setOnClickListener {
            val name = nameText_name.text.toString()
            if(name.isNotEmpty()){
                setView(0.5f,false)
                isCancelable = false
                Singleton.user.value?.let {
                    GlobalScope.launch (Dispatchers.IO){
                        async {
                            if(repo.saveName(it.uid, name)){
                                withContext(Dispatchers.Main){
                                    setView(1.0f,true)
                                    var user = it
                                    user.name = name
                                    Singleton.user.postValue(user)
                                    dismiss()
                                }
                            }else {
                                withContext(Dispatchers.Main){
                                    context?.let {c->
                                        isCancelable = true
                                        setView(1.0f,true)
                                        Toasty.error(c, "Error in saving name").show()
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

    }


    private fun setView(alpha : Float, enabled : Boolean){
        submitButton_name.alpha = alpha
        submitButton_name.isEnabled = enabled
    }
}
