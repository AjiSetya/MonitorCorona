package com.haidev.pantaucorona.features.kasus.views


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.haidev.pantaucorona.R
import com.haidev.pantaucorona.databinding.FragmentKasusBinding
import com.haidev.pantaucorona.features.kasus.models.KasusAttributes
import com.haidev.pantaucorona.features.kasus.models.KasusModel
import com.haidev.pantaucorona.features.kasus.viewmodels.KasusViewModel
import com.haidev.pantaucorona.preferences.AppModel
import com.haidev.pantaucorona.preferences.AppPreference
import kotlinx.android.synthetic.main.layout_bottomsheet.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class KasusFragment : Fragment() {

    private lateinit var kasusBinding: FragmentKasusBinding
    private lateinit var vmKasus: KasusViewModel

    private lateinit var mAppPreference: AppPreference
    private lateinit var appModel: AppModel
    private var listKasus: MutableList<KasusAttributes> = mutableListOf()
    private var listKasusProvinsi: MutableList<KasusAttributes> = mutableListOf()

    var dialogLoad: android.app.AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        kasusBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_kasus, container, false)
        vmKasus = ViewModelProvider(this).get(KasusViewModel::class.java)
        kasusBinding.kasus = vmKasus

        mAppPreference = AppPreference(context!!)
        appModel = mAppPreference.getPref()
        getData()

        kasusBinding.btnPilihLokasi.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.layout_bottomsheet, null)
            val dialog = BottomSheetDialog(context!!)
            view.close.setOnClickListener {
                dialog.dismiss()
            }

            val data =
                arrayOf(
                    "Maluku Utara",
                    "Maluku",
                    "Sulawesi Barat",
                    "Kepulauan Bangka Belitung",
                    "Papua Barat",
                    "Sulawesi Utara",
                    "Kalimantan Utara",
                    "Nusa Tenggara Barat",
                    "Sumatera Selatan",
                    "Jambi",
                    "Sulawesi Tenggara",
                    "Sulawesi Tengah",
                    "Riau",
                    "Kalimantan Selatan",
                    "Aceh",
                    "Kepulauan Riau",
                    "Kalimantan Tengah",
                    "Lampung",
                    "Sumatera Barat",
                    "Papua",
                    "Kalimantan Barat",
                    "Sumatera Utara",
                    "Kalimantan Timur",
                    "Daerah Istimewa Yogyakarta",
                    "Bali",
                    "Sulawesi Selatan",
                    "Jawa Tengah",
                    "Jawa Timur",
                    "Banten",
                    "Jawa Barat",
                    "DKI Jakarta"
                )
            view.numberPicker.minValue = 1
            view.numberPicker.maxValue = data.size
            view.numberPicker.displayedValues = data
            view.numberPicker.value = data.size

            view.btnTerapkan.setOnClickListener {
                getData()
                appModel.location = data[view.numberPicker.value - 1]
                mAppPreference.setPref(appModel)
                dialog.dismiss()
            }
            dialog.setContentView(view)
            dialog.show()
        }
        vmKasus.kasusResponse.observe(viewLifecycleOwner, Observer {
            onDataChange(it)
        })

        vmKasus.errorKasus.observe(viewLifecycleOwner, Observer {
            onErrorData(it)
        })

        kasusBinding.btnPilihLokasi.text = appModel.location

        return kasusBinding.root
    }

    fun getData() {
        val builder = android.app.AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.txtProgressBar)
        message.text = "Loading. . ."
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialogLoad = builder.create()
        dialogLoad!!.show()
        vmKasus.getDataKasus()
    }

    private fun onDataChange(it: MutableList<KasusModel>?) {
        dialogLoad?.dismiss()
        listKasus.clear()
        listKasusProvinsi.clear()

        for (i in it?.indices!!) {
            listKasus.add(it[i].attributes)
        }

        val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm:ss")
            current.format(formatter)
        } else {
            val dateNow = Calendar.getInstance()
            val formatter = SimpleDateFormat("dd-MM-yyy HH:mm:ss")
            formatter.format(dateNow.time)
        }

        kasusBinding.btnPilihLokasi.text = appModel.location
        kasusBinding.txtUpdate.text = "Terakhir di update $date"

        listKasusProvinsi.addAll(listKasus.filter { it.provinsi == appModel.location })

        kasusBinding.txtKasus.text = listKasusProvinsi[0].kasusPosi.toString()
        kasusBinding.txtMeninggal.text = listKasusProvinsi[0].kasusMeni.toString()
        kasusBinding.txtSembuh.text = listKasusProvinsi[0].kasusSemb.toString()
        var total: Int? =
            listKasusProvinsi[0].kasusPosi + listKasusProvinsi[0].kasusMeni + listKasusProvinsi[0].kasusSemb
        kasusBinding.txtTotalKasus.text = "dari total $total kasus"
    }

    private fun onErrorData(it: Throwable?) {
        dialogLoad?.dismiss()
    }


}